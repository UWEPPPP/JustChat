package www.raven.jc.entity.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChunkUploadBO {

  private String md5;
  private Integer chunkSize;
}
