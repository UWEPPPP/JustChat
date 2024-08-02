package www.raven.jc.service.impl;

import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.List;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import www.raven.jc.client.MyMinioClient;
import www.raven.jc.entity.bo.ChunkMergeBO;
import www.raven.jc.entity.bo.ChunkUploadBO;
import www.raven.jc.entity.bo.GetUrlBO;
import www.raven.jc.entity.bo.UploadBO;
import www.raven.jc.entity.vo.ChunkVO;
import www.raven.jc.entity.vo.GetUrlVO;
import www.raven.jc.service.FileService;

/**
 * file service impl
 *
 * @author 刘家辉
 * @date 2024/05/07
 */
@Service
public class FileServiceImpl implements FileService {

  @Resource
  private MyMinioClient myMinioClient;

  @Resource
  private RedissonClient redissonClient;

  @Override

  public void upload(UploadBO uploadBO) throws IOException {
    if (myMinioClient.exist(uploadBO.getMd5())) {
      return;
    }
    MultipartFile file = uploadBO.getFile();
    Assert.isTrue(myMinioClient.upload(file.getInputStream(), file.getSize(), uploadBO.getMd5()),
        "上传文件失败");
  }

  @Override
  public GetUrlVO getPresignedUrl(GetUrlBO getUrlBO) {
    String url = myMinioClient.getPresignedUrl(getUrlBO.getMd5());
    Assert.notNull(url, "获取预签名url失败");
    return new GetUrlVO().setUploadUrl(url).setPreviewUrl(myMinioClient.getUrl(getUrlBO.getMd5()));
  }

  @Override
  public ChunkVO getPresignedUrlForChunk(ChunkUploadBO chunkUploadBO) {
    String uploadId;
    if (myMinioClient.exist(chunkUploadBO.getMd5())) {
      //TODO 断点续传先不实现
      uploadId = redissonClient.getBucket(chunkUploadBO.getMd5()).get().toString();
    } else {
      uploadId = myMinioClient.createMultipartUpload(chunkUploadBO.getMd5());
      redissonClient.getBucket(chunkUploadBO.getMd5()).set(uploadId);
    }
    List<String> presignedObjectUrl = myMinioClient.getBatchPresignedUrlForChunk(uploadId,
        chunkUploadBO.getMd5(), chunkUploadBO.getChunkSize());
    Assert.notNull(presignedObjectUrl, "请求分片上传文件失败");
    return new ChunkVO().setUploadId(uploadId).setUrls(presignedObjectUrl)
        .setPreviewUrl(myMinioClient.getUrl(chunkUploadBO.getMd5()));
  }

  @Override
  public void chunkCompose(ChunkMergeBO chunkMergeBO) {
    Assert.isTrue(
        myMinioClient.completeMultipartUpload(chunkMergeBO.getMd5(), chunkMergeBO.getUploadId()),
        "合并分片失败");
  }

  @Override
  public String getUrl(String objectName) {
    Assert.notNull(objectName, "获取链接失败，objectName不能为空");
    return myMinioClient.getUrl(objectName);
  }
}
