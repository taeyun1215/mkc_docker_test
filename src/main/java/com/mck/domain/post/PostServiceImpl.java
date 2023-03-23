package com.mck.domain.post;

import com.mck.domain.image.Image;
import com.mck.domain.image.ImageRepo;
import com.mck.domain.image.ImageService;
import com.mck.domain.post.repo.PostRepo;
import com.mck.domain.post.request.PostDto;
import com.mck.domain.postlike.PostLike;
import com.mck.domain.postlike.PostLikeRepo;
import com.mck.domain.user.User;
import com.mck.domain.user.UserRepo;
import com.mck.global.error.BusinessException;
import com.mck.global.error.ErrorCode;
import com.mck.infra.image.AwsS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final PostLikeRepo postLikeRepo;
    private final ImageRepo imageRepo;

    private final ImageService imageService;
    private final AwsS3Service awsS3Service;

    @Override
    @Transactional
    public Page<Post> pagePostList(Pageable pageable) {
        return postRepo.findAll(pageable);
    }

    @Override
    @Transactional
    public Post viewDetailPost(Long postId) {
        return postRepo.findById(postId).get();

    }

    @Override
    @Transactional
    public Page<Post> searchPost(String keyword, Pageable pageable) {
        return postRepo.findAllSearch(keyword, pageable);
    }

    @Override
    @Transactional
    public List<Post> getPostAll() {
        log.info("모든 게시글을 가져옵니다.");
        return postRepo.findAll();
    }

    @Override
    @Transactional
    public Post savePost(PostDto postDto, User user) throws IOException {
        User findUser = userRepo.findById(user.getId()) // 스프링으로 로그인한 회원을 가져오지만 한번 더 DB에 있는지 조회함.
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        Post post = postDto.toEntity(findUser);
        Post savePost = postRepo.save(post);
        log.info("새로운 게시글 정보를 DB에 저장했습니다 : ", savePost.getTitle());

        if (postDto.getImageFiles() != null) {
            List<Image> images = awsS3Service.uploadFile(post, postDto.getImageFiles());
            savePost.setImages(images);
        }

        return savePost;
    }

    @Override
    @Transactional
    public void editPost(Long postId, PostDto postDto, User user) throws IOException {
        User findUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        Post findPost = postRepo.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        validateEditPost(postId, findUser); // 유효성 검사
        postRepo.editPost(postDto.getTitle(), postDto.getContent(), postId);
        log.info("게시글 정보를 업데이트 했습니다 : ", postDto.getTitle());

        awsS3Service.updateFile(findPost, findPost.getImages(), postDto);

        log.info("게시글에 이미지를 업데이트 했습니다. ");

    }

    @Transactional
    public void validateEditPost(Long postId, User findUser) {
        Optional<Post> findPostId = postRepo.findById(postId);
        Optional<Post> findPostIdAndUserId = postRepo.findByIdAndUser(postId, findUser);

        if (findPostId.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_EXIST_POST);
        } else if (findPostIdAndUserId.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_EDIT_PERMISSION_POST);
        }
    }

    @Override
    @Transactional
    public void deletePost(Long postId, User user) throws IOException {
        User findUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        Post findPost = postRepo.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        validateDeletePost(postId, findUser);  // 유효성 검사

        List<Image> images = findPost.getImages();
        images.forEach( image -> {
            awsS3Service.deleteFile(image.getImageName());
        });

        log.info("로컬에 이미지를 삭제했습니다 : ", findPost.getImages());

        postRepo.delete(findPost);
        log.info("게시글을 삭제하였습니다 : ", findPost.getTitle());

    }

    @Transactional
    public void validateDeletePost(Long postId, User findUser) {
        Optional<Post> findPostId = postRepo.findById(postId);
        Optional<Post> findPostIdAndUserId = postRepo.findByIdAndUser(postId, findUser);

        if (findPostId.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_EXIST_POST);
        } else if (findPostIdAndUserId.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_DELETE_PERMISSION_POST);
        }
    }

    @Override
    @Transactional
    public String likePost(Long postId, User user) {
        User findUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        Post findPost = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST));

        Optional<PostLike> findPostLike = postLikeRepo.findByPostAndUser(findPost, findUser);
        AtomicReference<String> returnObject = new AtomicReference<>();

        findPostLike.ifPresentOrElse(
                postLike -> {
                    postLikeRepo.delete(postLike);
                    returnObject.set("좋아요가 삭제되었습니다.");
                },
                () -> {
                    PostLike savePostLike = PostLike.builder()
                            .post(findPost)
                            .user(findUser)
                            .build();

                    postLikeRepo.save(savePostLike);
                    returnObject.set("좋아요가 추가되었습니다.");
                }
        );

        return returnObject.get();
    }

    @Override
    @Transactional
    public void updateViewPost(Long postId) {
        Post findPost = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST));

        postRepo.updateView(findPost.getId());
        log.info("게시글을 조회했습니다.");
    }

    @Override
    @Transactional
    public List<Post> popularPost() {
        return postRepo.popularPost();
    }

    @Override
    @Transactional
    public List<Post> myPost(String username) {
        return postRepo.myPost(username);
    }

}
