package com.example.jobservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.Job.JobWithRecruiterDTO;
import org.example.common.dto.JobSeeker.JobSeekerWithUserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@EnableCaching
public class GeminiService {
    @Value("${gemini.api-key}")
    private String GEMINI_API_KEY;

    @Value("${gemini.base-url}")
    private String GEMINI_BASE_URL;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Cacheable(value = "suggestJobSeekers", key = "#job.id")
    public List<JobSeekerWithUserDTO> suggestJobSeekers(JobWithRecruiterDTO job, List<JobSeekerWithUserDTO> seekers) throws Exception {
        String prompt = buildSuggestJobSeekersPrompt(job, seekers);
        String geminiResponse = callGemini(prompt);
        List<String> suggestedSeekerIds = extractIds(geminiResponse);

        return seekers.stream()
                .filter(seeker -> suggestedSeekerIds.contains(seeker.getId()))
                .toList();
    }

    @Cacheable(value = "suggestJobs", key = "#jobSeeker.id")
    public List<JobWithRecruiterDTO> suggestJobs(JobSeekerWithUserDTO jobSeeker, List<JobWithRecruiterDTO> jos) throws Exception {
        String prompt = buildSuggestJobsPrompt(jobSeeker, jos);
        String geminiResponse = callGemini(prompt);
        List<String> suggestedJobIds = extractIds(geminiResponse);

        return jos.stream().filter(job -> suggestedJobIds.contains(job.getId())).toList();
    }

    private String buildSuggestJobSeekersPrompt(JobWithRecruiterDTO job, List<JobSeekerWithUserDTO> seekers) throws JsonProcessingException {
        String jobJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(job);
        String seekersJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(seekers);

        return """
                 I have a job posting with the following details:
                 %s
                
                 And a list of job seekers:
                 %s
                
                 Please suggest as most as suitable job seekers for this job posting. Return a JSON array of job seeker IDs only, like:
                 ["seekerId1", "seekerId2", "seekerId3"]
                """.formatted(jobJson, seekersJson);
    }

    private String buildSuggestJobsPrompt(JobSeekerWithUserDTO seeker, List<JobWithRecruiterDTO> jobs) throws JsonProcessingException {
        String seekerJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(seeker);
        String jobsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jobs);

        return """
                 I have a job seeker with the following profile:
                 %s
                
                 And a list of job postings:
                 %s
                
                 Please suggest as most as suitable jobs for this job seeker. Return a JSON array of job IDs only, like:
                 ["jobId1", "jobId2", "jobId3"]
                """.formatted(seekerJson, jobsJson);
    }

    private List<String> extractIds(String geminiResponse) throws JsonProcessingException {
        JsonNode root = objectMapper.readTree(geminiResponse);
        String content = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        int startIdx = content.indexOf("[");
        int endIdx = content.indexOf("]", startIdx) + 1;

        if (startIdx == -1) {
            throw new IllegalArgumentException("Invalid Gemini response: " + content);
        }

        String jsonArray = content.substring(startIdx, endIdx);
        return objectMapper.readValue(jsonArray, new TypeReference<>() {});
    }

    private String callGemini(String prompt) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": %s }
                      ]
                    }
                  ]
                }
                """.formatted(new ObjectMapper().writeValueAsString(prompt));

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        String url = GEMINI_BASE_URL + "?key=" + GEMINI_API_KEY;

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        return response.getBody();
    }
}
