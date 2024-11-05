package www.raven.jc.client;

import com.google.common.collect.Multimap;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.ListMultipartUploadsResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Part;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyMinioClient extends MinioClient {

	private final String domain;
	private final String bucket;
	private final String exposeDomain;

	public MyMinioClient(MinioClient client, String domain, String bucket,
	                     String exposeDomain) {
		super(client);
		this.domain = domain;
		this.bucket = bucket;
		this.exposeDomain = exposeDomain;
	}

	public boolean upload(InputStream inputStream, long fileSize,
	                      String objectName) {
		try {
			super.putObject(
					PutObjectArgs.builder()
							.bucket(bucket)
							.object(objectName)
							.stream(inputStream, fileSize, -1)
							.build()
			);
			return true;
		} catch (Exception e) {
			log.error("{} minio上传文件 {} 失败:{}", LocalDateTime.now(), objectName, e.getMessage());
			return false;
		}
	}

	public String getUrl(String objectName) {
		return exposeDomain + "/" + bucket + "/" + objectName;
	}

	public boolean exist(String objectName) {
		try {
			super.statObject(StatObjectArgs.builder().bucket(bucket).object(objectName).build());
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String createMultipartUpload(String objectName) {
		try {
			return super.createMultipartUpload(bucket, null, objectName, null, null).result().uploadId();
		} catch (Exception e) {
			log.error("{} minio创建 {} 分片任务失败:{}", LocalDateTime.now(), objectName, e.getMessage());
			return null;
		}
	}

	public String getPresignedUrl(String objectName) {
		try {
			return super.getPresignedObjectUrl(
					GetPresignedObjectUrlArgs.builder()
							.method(Method.PUT)
							.object(objectName)
							.bucket(bucket)
							.build()
			);
		} catch (Exception e) {
			log.error("{} minio获取 {} 预签名url失败:{}", LocalDateTime.now(), objectName,
					e.getMessage());
			return null;
		}
	}

	public List<String> getBatchPresignedUrlForChunk(String uploadId,
	                                                 String objectName, int chunkSize) {
		List<String> result = new ArrayList<>();

		Map<String, String> reqParams = new HashMap<>();
		reqParams.put("uploadId", uploadId);

		try {
			for (int i = 1; i <= chunkSize; i++) {
				reqParams.put("partNumber", String.valueOf(i));
				result.add(super.getPresignedObjectUrl(
						GetPresignedObjectUrlArgs.builder()
								.method(Method.PUT)
								.object(objectName)
								.bucket(bucket)
								.extraQueryParams(reqParams)
								.build()
				));
			}
			return result;
		} catch (Exception e) {
			log.error("{} minio获取 {} 分片任务url失败, uploadId为{}:{}", LocalDateTime.now(), objectName,
					uploadId, e.getMessage());
			return null;
		}
	}

	@Override
	public ListMultipartUploadsResponse listMultipartUploads(String bucketName,
	                                                         String region, String delimiter, String encodingType, String keyMarker,
	                                                         Integer maxUploads, String prefix, String uploadIdMarker,
	                                                         Multimap<String, String> extraHeaders,
	                                                         Multimap<String, String> extraQueryParams) {
		try {
			return super.listMultipartUploads(bucketName, region, delimiter, encodingType, keyMarker,
					maxUploads, prefix, uploadIdMarker, extraHeaders, extraQueryParams);
		} catch (InsufficientDataException | IOException | InvalidKeyException |
		         ServerException | XmlParserException | ErrorResponseException |
		         InternalException | NoSuchAlgorithmException |
		         InvalidResponseException e) {
			log.error("listMultipartUploads error:{}", e.getMessage());
			return null;
		}
	}

	public boolean completeMultipartUpload(String objectName, String uploadId) {
		try {
			super.completeMultipartUpload(
					bucket,
					null,
					objectName,
					uploadId,
					super.listParts(bucket, null, objectName, null, null, uploadId, null, null).result()
							.partList().toArray(new Part[]{}),
					null,
					null
			);
			return true;
		} catch (Exception e) {
			log.error("minio合并文件 {} 失败, uploadId为{}:{}", objectName, uploadId, e.getMessage());
			return false;
		}
	}
}
