package www.raven.jc.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class GetUrlVO {

	private String uploadUrl;
	private String previewUrl;
}
