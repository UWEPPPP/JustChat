package www.raven.jc.entity.bo;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UploadBO {

  private MultipartFile file;
  private String md5;
}
