package www.raven.jc.service;

import java.io.IOException;
import www.raven.jc.entity.bo.ChunkMergeBO;
import www.raven.jc.entity.bo.ChunkUploadBO;
import www.raven.jc.entity.bo.GetUrlBO;
import www.raven.jc.entity.bo.UploadBO;
import www.raven.jc.entity.vo.ChunkVO;
import www.raven.jc.entity.vo.GetUrlVO;

public interface FileService {

  /**
   * 服务端上传文件的方式
   */
  void upload(UploadBO uploadBO) throws IOException;

  /**
   * 获得预签名URL
   */
  GetUrlVO getPresignedUrl(GetUrlBO getUrlBO);

  ChunkVO getPresignedUrlForChunk(ChunkUploadBO uploadBO);

  void chunkCompose(ChunkMergeBO chunkMergeBO);

  String getUrl(String objectName);
}
