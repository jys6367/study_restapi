package me.hobbang.demohobbangrestapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hobbang.demohobbangrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("spring REST API TEST!")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 03, 10, 4, 2, 2))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 03, 11, 4, 2, 2))
                .beginEventDateTime(LocalDateTime.of(2020, 03, 12, 4, 2, 2))
                .endEventDateTime(LocalDateTime.of(2020, 03, 13, 4, 2, 2))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("중랑구 소방서")
                .build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
//                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT))  왜안되지
                .andExpect(jsonPath("eventStatus").value("DRAFT"));

    }

    @Test
    @TestDescription("입력 받을 수 없는 값을 사용한 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(10)
                .name("Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 03, 10, 4, 2, 2))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 03, 11, 4, 2, 2))
                .beginEventDateTime(LocalDateTime.of(2020, 03, 12, 4, 2, 2))
                .endEventDateTime(LocalDateTime.of(2020, 03, 13, 4, 2, 2))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("중랑구 소방서")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

        mockMvc.perform(
                post("/api/events/")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaTypes.HAL_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        var eventDto = EventDto.builder().build();

        this.mockMvc.perform(
                post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());

    }

    // 끝나는 나라ㅉ가 더 빨라..
    // 맥스가 더 작아.. 베이스보다
    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("Spring REST API TEST")
                .beginEnrollmentDateTime(LocalDateTime.of(2020, 03, 10, 4, 2, 2))
                .closeEnrollmentDateTime(LocalDateTime.of(2020, 03, 19, 4, 2, 2))
                .beginEventDateTime(LocalDateTime.of(2020, 03, 12, 4, 2, 2))
                .endEventDateTime(LocalDateTime.of(2020, 03, 11, 4, 2, 2))
                .basePrice(50000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("중랑구 소방서")
                .build();

        this.mockMvc.perform(
                post("/api/events")
                        .contentType((MediaType.APPLICATION_JSON_UTF8))
                        .content(this.objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists())
                .andExpect(jsonPath("$[0].code").exists())
        ;
    }

}
