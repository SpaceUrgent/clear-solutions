package clear.solutions.test.assignment.controller;

import clear.solutions.test.assignment.configuration.UserConfigurationProperties;
import clear.solutions.test.assignment.dao.UserDao;
import clear.solutions.test.assignment.dto.DataDto;
import clear.solutions.test.assignment.dto.RegisterUserRequest;
import clear.solutions.test.assignment.dto.RegisterUserResponse;
import clear.solutions.test.assignment.exception.Error;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String VALID_EMAIL = "username@domain.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Johnson";
    private static final String ADDRESS = "address";
    private static final String PHONE = "phone";

    private static RegisterUserRequest VALID_REGISTER_REQUEST;
    private static RegisterUserRequest REGISTER_REQUEST_WITHOUT_EMAIL;
    private static RegisterUserRequest REGISTER_REQUEST_WITH_INVALID_EMAIL;
    private static RegisterUserRequest REGISTER_REQUEST_WITH_NULL_FIRST_NAME;
    private static RegisterUserRequest REGISTER_REQUEST_WITH_BLANK_FIRST_NAME;
    private static RegisterUserRequest REGISTER_REQUEST_WITH_NULL_LAST_NAME;
    private static RegisterUserRequest REGISTER_REQUEST_WITH_BLANK_LAST_NAME;
    private static RegisterUserRequest REGISTER_REQUEST_WITH_NULL_BIRTH_DATE;
    private static RegisterUserRequest REGISTER_REQUEST_WITH_ILLEGAL_AGE;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserConfigurationProperties properties;
    @Autowired
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao.deleteAll();

        VALID_REGISTER_REQUEST = new RegisterUserRequest();
        VALID_REGISTER_REQUEST.setEmail(VALID_EMAIL);
        VALID_REGISTER_REQUEST.setFirstName(FIRST_NAME);
        VALID_REGISTER_REQUEST.setLastName(LAST_NAME);
        VALID_REGISTER_REQUEST.setBirthDate(LocalDate.now().minusYears(properties.getMinAge() + 1));
        VALID_REGISTER_REQUEST.setAddress(ADDRESS);
        VALID_REGISTER_REQUEST.setPhone(PHONE);

        REGISTER_REQUEST_WITHOUT_EMAIL = getCopyFrom(VALID_REGISTER_REQUEST);
        REGISTER_REQUEST_WITHOUT_EMAIL.setEmail(null);
        REGISTER_REQUEST_WITH_INVALID_EMAIL = getCopyFrom(VALID_REGISTER_REQUEST);
        REGISTER_REQUEST_WITH_INVALID_EMAIL.setEmail("invalid@email");
        REGISTER_REQUEST_WITH_NULL_FIRST_NAME = getCopyFrom(VALID_REGISTER_REQUEST);
        REGISTER_REQUEST_WITH_NULL_FIRST_NAME.setFirstName(null);
        REGISTER_REQUEST_WITH_BLANK_FIRST_NAME = getCopyFrom(VALID_REGISTER_REQUEST);
        REGISTER_REQUEST_WITH_BLANK_FIRST_NAME.setFirstName("");
        REGISTER_REQUEST_WITH_NULL_LAST_NAME = getCopyFrom(VALID_REGISTER_REQUEST);
        REGISTER_REQUEST_WITH_NULL_LAST_NAME.setLastName(null);
        REGISTER_REQUEST_WITH_BLANK_LAST_NAME = getCopyFrom(VALID_REGISTER_REQUEST);
        REGISTER_REQUEST_WITH_BLANK_LAST_NAME.setLastName("");
        REGISTER_REQUEST_WITH_NULL_BIRTH_DATE = getCopyFrom(VALID_REGISTER_REQUEST);
        REGISTER_REQUEST_WITH_NULL_BIRTH_DATE.setBirthDate(null);
        REGISTER_REQUEST_WITH_ILLEGAL_AGE = getCopyFrom(VALID_REGISTER_REQUEST);
        REGISTER_REQUEST_WITH_ILLEGAL_AGE.setBirthDate(LocalDate.now().minusYears(properties.getMinAge()).plusDays(1));
    }

    @Test
    @DisplayName("Register valid user - OK")
    void register_ok() throws Exception {
        final var mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(VALID_REGISTER_REQUEST))))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.email").value(VALID_REGISTER_REQUEST.getEmail()))
                .andExpect(jsonPath("$.data.firstName").value(VALID_REGISTER_REQUEST.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(VALID_REGISTER_REQUEST.getLastName()))
                .andExpect(jsonPath("$.data.birthDate").value(VALID_REGISTER_REQUEST.getBirthDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.data.address").value(VALID_REGISTER_REQUEST.getAddress()))
                .andExpect(jsonPath("$.data.phone").value(VALID_REGISTER_REQUEST.getPhone()))
                .andReturn();

        final var response = readJson(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<DataDto<RegisterUserResponse>>() {});
        final var id = response.getData().getId();
        assertEquals("/users/%d".formatted(id), mvcResult.getResponse().getHeader(HttpHeaders.LOCATION));
        final var saved = userDao.findById(id)
                .orElse(null);
        assertNotNull(saved);
        assertEquals(VALID_REGISTER_REQUEST.getEmail(), saved.getEmail());
        assertEquals(VALID_REGISTER_REQUEST.getFirstName(), saved.getFirstName());
        assertEquals(VALID_REGISTER_REQUEST.getLastName(), saved.getLastName());
        assertEquals(VALID_REGISTER_REQUEST.getBirthDate(), saved.getBirthDate());
        assertEquals(VALID_REGISTER_REQUEST.getAddress(), saved.getAddress());
        assertEquals(VALID_REGISTER_REQUEST.getPhone(), saved.getPhone());
    }

    @Test
    @DisplayName("Register user with null email returns 400")
    void register_withNullEmail_returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(REGISTER_REQUEST_WITHOUT_EMAIL))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad request, missing or invalid request arguments"))
                .andExpect(jsonPath("$.details").value(
                        Matchers.hasEntry("data.email", "Email must be present")))
                .andExpect(jsonPath("$.path").value("/users"));
        assertEquals(0, userDao.countAll());
    }

    @Test
    @DisplayName("Register user with null email returns 400")
    void register_withInvalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(REGISTER_REQUEST_WITH_INVALID_EMAIL))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad request, missing or invalid request arguments"))
                .andExpect(jsonPath("$.details").value(
                        Matchers.hasEntry("data.email", "Invalid email format")))
                .andExpect(jsonPath("$.path").value("/users"));
        assertEquals(0, userDao.countAll());
    }

    @Test
    @DisplayName("Register user with null or blank first name returns 400")
    void register_withInvalidFirstName_returns400() throws Exception {
        for (RegisterUserRequest request : List.of(REGISTER_REQUEST_WITH_NULL_FIRST_NAME, REGISTER_REQUEST_WITH_BLANK_FIRST_NAME)) {
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(writeJson(DataDto.of(request))))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.reason").value("Bad request, missing or invalid request arguments"))
                    .andExpect(jsonPath("$.details").value(
                            Matchers.hasEntry("data.firstName", "First name must be present and contains at least 1 symbol")))
                    .andExpect(jsonPath("$.path").value("/users"));
            assertEquals(0, userDao.countAll());
        }
    }

    @Test
    @DisplayName("Register user with null or blank last name returns 400")
    void register_withInvalidLastName_returns400() throws Exception {
        for (RegisterUserRequest request : List.of(REGISTER_REQUEST_WITH_NULL_LAST_NAME, REGISTER_REQUEST_WITH_BLANK_LAST_NAME)) {
            mockMvc.perform(post("/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(writeJson(DataDto.of(request))))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.timestamp").exists())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.reason").value("Bad request, missing or invalid request arguments"))
                    .andExpect(jsonPath("$.details").value(
                            Matchers.hasEntry("data.lastName", "Last name must be present and contains at least 1 symbol")))
                    .andExpect(jsonPath("$.path").value("/users"));
            assertEquals(0, userDao.countAll());
        }
    }

    @Test
    @DisplayName("Register user with null birth date returns 400")
    void register_withNullBirthDate_returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(REGISTER_REQUEST_WITH_NULL_BIRTH_DATE))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad request, missing or invalid request arguments"))
                .andExpect(jsonPath("$.details").value(
                        Matchers.hasEntry("data.birthDate", "Birth date must be present")))
                .andExpect(jsonPath("$.path").value("/users"));
        assertEquals(0, userDao.countAll());
    }

    @Test
    @DisplayName("Register user with age below min returns 422")
    void register_withAgeBelowMin_returns422() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(REGISTER_REQUEST_WITH_ILLEGAL_AGE))))
                .andDo(print())
                .andExpect(status().is(Error.INVALID_AGE.getHttpStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(Error.INVALID_AGE.getHttpStatus().value()))
                .andExpect(jsonPath("$.reason").value(Error.INVALID_AGE.getReason()))
                .andExpect(jsonPath("$.path").value("/users"));
        assertEquals(0, userDao.countAll());
    }

    private <T> T readJson(final byte[] json, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(json, typeReference);
    }

    private byte[] writeJson(final Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(object);
    }

    private RegisterUserRequest getCopyFrom(RegisterUserRequest validRegisterRequest) {
        final var copy = new RegisterUserRequest();
        BeanUtils.copyProperties(validRegisterRequest, copy);
        return copy;
    }
}