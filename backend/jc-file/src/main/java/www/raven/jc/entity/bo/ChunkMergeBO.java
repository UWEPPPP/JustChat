package www.raven.jc.entity.bo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChunkMergeBO {

  private String uploadId;
  private String md5;
}
