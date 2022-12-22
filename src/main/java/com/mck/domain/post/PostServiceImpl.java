package com.mck.domain.post;

import com.mck.domain.image.Image;
import com.mck.domain.image.ImageService;
import com.mck.domain.postlike.PostLike;
import com.mck.domain.postlike.PostLikeRepo;
import com.mck.domain.user.User;
import com.mck.domain.user.UserRepo;
import com.mck.global.error.BusinessException;
import com.mck.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepo postRepo;
    private final UserRepo userRepo;
    private final PostLikeRepo postLikeRepo;

    private final ImageService imageService;

    @Override
    @Transactional
    public Page<Post> pagePostList(Pageable pageable) {
        return postRepo.findAll(pageable);
    }

    @Override
    @Transactional
    public Page<Post> searchPost(String keyword, Pageable pageable) {
        return null; // postRepo.findAllSearch(keyword, pageable);
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

        List<Image> saveImageFiles = imageService.saveImages(savePost, postDto.getImageFiles());
        log.info("새로운 게시글 이미지들을 DB에 저장했습니다 : ", savePost.getTitle());

        savePost.setImages(saveImageFiles);

        return savePost;
    }

    @Override
    @Transactional
    public void editPost(Long postId, PostDto postDto, User user) throws IOException {
        User findUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        validateEditPost(postId, findUser); // 유효성 검사
        postRepo.editPost(postDto.getTitle(), postDto.getContent(), postId);
        log.info("게시글 정보를 업데이트 했습니다 : ", postDto.getTitle());

        Optional<Post> findPost = postRepo.findById(postId);
        List<MultipartFile> imageFiles = postDto.getImageFiles();

        imageService.updateImage(imageFiles, findPost.get());
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

        validateDeletePost(postId, findUser);  // 유효성 검사
        Optional<Post> findPost = postRepo.findById(postId);

        imageService.deleteImage(findPost.get());
        log.info("로컬에 이미지를 삭제했습니다 : ", findPost.get().getImages());

        postRepo.delete(findPost.get());
        log.info("게시글을 삭제하였습니다 : ", findPost.get().getTitle());

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
    public void likePost(Long postId, User user) {
        User findUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        Post findPost = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST));

        Optional<PostLike> findPostLike = postLikeRepo.findByPostAndUser(findPost, findUser);

        findPostLike.ifPresentOrElse(
                postLike -> {
                    postLikeRepo.delete(postLike);
                },
                () -> {
                    PostLike savePostLike = PostLike.builder()
                            .post(findPost)
                            .user(findUser)
                            .build();

                    postLikeRepo.save(savePostLike);
                }
        );
    }

    @Override
    @Transactional
    public Post updateViewPost(Long postId) {
        Post findPost = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST));

        postRepo.updateView(findPost.getId());
        log.info("게시글을 조회했습니다.");
        return findPost;
    }


}
