package com.chenfuzhu.aipicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chenfuzhu.aipicturebackend.model.dto.picture.PictureQueryRequest;
import com.chenfuzhu.aipicturebackend.model.dto.picture.PictureUploadRequest;
import com.chenfuzhu.aipicturebackend.model.entity.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chenfuzhu.aipicturebackend.model.entity.User;
import com.chenfuzhu.aipicturebackend.model.vo.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author 21993
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-05-23 16:19:39
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    public PictureVO uploadPicture(MultipartFile multipartFile,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    public PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    public void validPicture(Picture picture);
}
