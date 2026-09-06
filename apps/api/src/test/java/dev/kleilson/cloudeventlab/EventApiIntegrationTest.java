package dev.kleilson.cloudeventlab;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventApiIntegrationTest {

  @Autowired MockMvc mockMvc;

  @Test
  void ingestAndGetEvent() throws Exception {
    String body =
        """
        {
          "type": "order.created",
          "source": "integration-test",
          "idempotencyKey": "it-1",
          "payloadJson": "{\\"orderId\\":\\"A1\\"}"
        }
        """;

    MvcResult created =
        mockMvc
            .perform(
                post("/api/events")
                    .with(httpBasic("lab", "lab-change-me"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isAccepted())
            .andExpect(jsonPath("$.status").value("QUEUED"))
            .andReturn();

    String id =
        com.jayway.jsonpath.JsonPath.read(created.getResponse().getContentAsString(), "$.id");

    String status = "QUEUED";
    for (int i = 0; i < 20 && !"PROCESSED".equals(status) && !"FAILED".equals(status); i++) {
      Thread.sleep(100);
      MvcResult got =
          mockMvc
              .perform(get("/api/events/" + id).with(httpBasic("lab", "lab-change-me")))
              .andExpect(status().isOk())
              .andReturn();
      status = com.jayway.jsonpath.JsonPath.read(got.getResponse().getContentAsString(), "$.status");
    }

    mockMvc
        .perform(get("/api/events/" + id).with(httpBasic("lab", "lab-change-me")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("PROCESSED"));
  }

  @Test
  void labStatusIsPublic() throws Exception {
    mockMvc
        .perform(get("/api/lab/status"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.service").value("cloud-event-lab-api"));
  }
}
