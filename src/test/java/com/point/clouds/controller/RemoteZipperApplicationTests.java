package com.point.clouds.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootTest
class FilesUploadZipperTests {

	public static final String FILE_URL = "http://localhost:8080/zipuploaded";
	private final Path zipped = Paths.get("zipped");

	RestTemplate restTemplate;

	@Autowired
	ObjectMapper mapper;

	@BeforeEach
	public void setUp() {
		mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
		restTemplate = new RestTemplate();
		if (!zipped.toFile().exists())
			init();
	}

	public void init() {
		try {
			Files.createDirectory(zipped);
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}

	@Test
	void givenBulkFilesUploadUrl_whenUploading_thenExpectReturningOneZippedFileOutStreamForDownload() throws IOException {
		uploadMultipleFilesAndDownload();
	}

	private void uploadMultipleFilesAndDownload() throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
		File file = zipped.resolve("compressedOfFiles.zip").toFile();
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("files", getTestFile());
		body.add("files", getTestFile());
		body.add("files", getTestFile());
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		RequestCallback requestCallback = restTemplate.httpEntityCallback(requestEntity, StreamingResponseBody.class);
		restTemplate.execute(
				FILE_URL,
				HttpMethod.POST,
				requestCallback,
				clientHttpResponse -> {
					List<String> headerValues = clientHttpResponse.getHeaders().get("Content-Disposition");
					StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(file, false));
					System.out.println("!!!!Returning zipped file path " + file.getAbsolutePath());
					return file;
				});
		Assertions.assertThat(file).isNotNull();
	}

	public static Resource getTestFile() throws IOException {
		Path testFile = Files.createTempFile("test-file", ".txt");
		Files.write(testFile, "I am one of a number of files that is supposed to be zipped in the server and return".getBytes());
		return new FileSystemResource(testFile.toFile());
	}

}
