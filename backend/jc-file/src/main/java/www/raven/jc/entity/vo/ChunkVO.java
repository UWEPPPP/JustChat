package www.raven.jc.entity.vo;

import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChunkVO {

  private String uploadId;
  private List<String> urls;
  private String previewUrl;
}
