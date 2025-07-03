package com.lpk.userservice.service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ChatService {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String getChatResponse(String userMessage) throws IOException {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");

        String requestBody = "{\n" +
                "\"model\": \"gpt-3.5-turbo\",\n" +
                "\"messages\": [{\"role\": \"user\", \"content\": \"" + userMessage + "\"}]\n" +
                "}";

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .post(RequestBody.create(requestBody, mediaType))
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
