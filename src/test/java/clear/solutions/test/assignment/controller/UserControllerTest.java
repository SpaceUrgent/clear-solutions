package clear.solutions.test.assignment.controller;

import clear.solutions.test.assignment.configuration.UserConfigurationProperties;
import clear.solutions.test.assignment.dao.UserDao;
import clear.solutions.test.assignment.dto.DataDto;
import clear.solutions.test.assignment.dto.CreateUserDto;
import clear.solutions.test.assignment.dto.UserContactsDto;
import clear.solutions.test.assignment.dto.UserDto;
import clear.solutions.test.assignment.exception.Error;
import clear.solutions.test.assignment.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private static final String NEW_VALID_EMAIL = "username100@domain.com";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Johnson";
    private static final String ADDRESS = "address";
    private static final String NEW_ADDRESS = "new address";
    private static final String PHONE = "phone";
    private static final String NEW_PHONE = "new phone";
    private static final Long NON_EXISTING_USER_ID = Long.MAX_VALUE;

    private static User USER;
    private static CreateUserDto VALID_REGISTER_REQUEST;
    private static CreateUserDto REGISTER_REQUEST_WITHOUT_EMAIL;
    private static CreateUserDto REGISTER_REQUEST_WITH_INVALID_EMAIL;
    private static CreateUserDto REGISTER_REQUEST_WITH_NULL_FIRST_NAME;
    private static CreateUserDto REGISTER_REQUEST_WITH_BLANK_FIRST_NAME;
    private static CreateUserDto REGISTER_REQUEST_WITH_NULL_LAST_NAME;
    private static CreateUserDto REGISTER_REQUEST_WITH_BLANK_LAST_NAME;
    private static CreateUserDto REGISTER_REQUEST_WITH_NULL_BIRTH_DATE;
    private static CreateUserDto REGISTER_REQUEST_WITH_ILLEGAL_AGE;
    private static UserContactsDto PATCH_USER_CONTACTS_REQUEST;
    private static UserContactsDto PATCH_USER_EMAIL_REQUEST;
    private static UserContactsDto PATCH_USER_ADDRESS_REQUEST;
    private static UserContactsDto PATCH_USER_PHONE_REQUEST;
    private static UserContactsDto PATCH_USER_REQUEST_WITH_INVALID_EMAIL;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserConfigurationProperties properties;
    @SpyBean
    private UserDao userDao;

    @BeforeEach
    void setUp() {
        userDao.deleteAll();

        final var validBirthDate = LocalDate.now().minusYears(properties.getMinAge() + 1);

        USER = new User();
        USER.setEmail(VALID_EMAIL);
        USER.setFirstName(FIRST_NAME);
        USER.setLastName(LAST_NAME);
        USER.setBirthDate(validBirthDate);
        final var saved = userDao.save(USER);
        assertNotSame(USER, saved, "User dao save must return a clone of saved entity");
        USER = saved;
        Mockito.clearInvocations(userDao);

        VALID_REGISTER_REQUEST = new CreateUserDto();
        VALID_REGISTER_REQUEST.setEmail(VALID_EMAIL);
        VALID_REGISTER_REQUEST.setFirstName(FIRST_NAME);
        VALID_REGISTER_REQUEST.setLastName(LAST_NAME);
        VALID_REGISTER_REQUEST.setBirthDate(validBirthDate);
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

        PATCH_USER_CONTACTS_REQUEST = new UserContactsDto();
        PATCH_USER_CONTACTS_REQUEST.setEmail(NEW_VALID_EMAIL);
        PATCH_USER_CONTACTS_REQUEST.setAddress(NEW_ADDRESS);
        PATCH_USER_CONTACTS_REQUEST.setPhone(NEW_PHONE);
        PATCH_USER_EMAIL_REQUEST = new UserContactsDto();
        PATCH_USER_EMAIL_REQUEST.setEmail(NEW_VALID_EMAIL);
        PATCH_USER_ADDRESS_REQUEST = new UserContactsDto();
        PATCH_USER_ADDRESS_REQUEST.setAddress(NEW_ADDRESS);
        PATCH_USER_PHONE_REQUEST = new UserContactsDto();
        PATCH_USER_PHONE_REQUEST.setPhone(NEW_PHONE);
        PATCH_USER_REQUEST_WITH_INVALID_EMAIL = new UserContactsDto();
        PATCH_USER_REQUEST_WITH_INVALID_EMAIL.setEmail("invalid@mail");
    }

    @Test
    @DisplayName("Register user - OK")
    void register_ok() throws Exception {
        final var mvcResult = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(VALID_REGISTER_REQUEST))))
                .andDo(print())
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

        final var response = readJson(mvcResult.getResponse().getContentAsByteArray(), new TypeReference<DataDto<UserDto>>() {});
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
    @DisplayName("Register user with null data returns 400")
    void register_withNullData_returns400() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(new DataDto<>())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad request, missing or invalid request arguments"))
                .andExpect(jsonPath("$.details").value(
                        Matchers.hasEntry("data", "Data must be present")))
                .andExpect(jsonPath("$.path").value("/users"));
        verify(userDao, times(0)).save(any());
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
        verify(userDao, times(0)).save(any());
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
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Register user with null or blank first name returns 400")
    void register_withInvalidFirstName_returns400() throws Exception {
        for (CreateUserDto request : List.of(REGISTER_REQUEST_WITH_NULL_FIRST_NAME, REGISTER_REQUEST_WITH_BLANK_FIRST_NAME)) {
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
            verify(userDao, times(0)).save(any());
        }
    }

    @Test
    @DisplayName("Register user with null or blank last name returns 400")
    void register_withInvalidLastName_returns400() throws Exception {
        for (CreateUserDto request : List.of(REGISTER_REQUEST_WITH_NULL_LAST_NAME, REGISTER_REQUEST_WITH_BLANK_LAST_NAME)) {
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
            verify(userDao, times(0)).save(any());
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
        verify(userDao, times(0)).save(any());
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
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Update user - OK")
    void updateUser_ok() throws Exception {
        mockMvc.perform(put("/users/{userId}", USER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(VALID_REGISTER_REQUEST))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(USER.getId()))
                .andExpect(jsonPath("$.data.email").value(VALID_REGISTER_REQUEST.getEmail()))
                .andExpect(jsonPath("$.data.firstName").value(VALID_REGISTER_REQUEST.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(VALID_REGISTER_REQUEST.getLastName()))
                .andExpect(jsonPath("$.data.birthDate").value(VALID_REGISTER_REQUEST.getBirthDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.data.address").value(VALID_REGISTER_REQUEST.getAddress()))
                .andExpect(jsonPath("$.data.phone").value(VALID_REGISTER_REQUEST.getPhone()))
                .andReturn();

        final var updated = userDao.findById(USER.getId())
                .orElse(null);
        assertNotNull(updated);
        assertEquals(VALID_REGISTER_REQUEST.getEmail(), updated.getEmail());
        assertEquals(VALID_REGISTER_REQUEST.getFirstName(), updated.getFirstName());
        assertEquals(VALID_REGISTER_REQUEST.getLastName(), updated.getLastName());
        assertEquals(VALID_REGISTER_REQUEST.getBirthDate(), updated.getBirthDate());
        assertEquals(VALID_REGISTER_REQUEST.getAddress(), updated.getAddress());
        assertEquals(VALID_REGISTER_REQUEST.getPhone(), updated.getPhone());
    }

    @Test
    @DisplayName("Update user with null data returns 400")
    void updateUser_withNullData_returns400() throws Exception {
        mockMvc.perform(put("/users/{userId}", USER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(new DataDto<>())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad request, missing or invalid request arguments"))
                .andExpect(jsonPath("$.details").value(
                        Matchers.hasEntry("data", "Data must be present")))
                .andExpect(jsonPath("$.path").value("/users/%d".formatted(USER.getId())));
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Update user with null email returns 400")
    void updateUser_withNullEmail_returns400() throws Exception {
        mockMvc.perform(put("/users/{userId}", USER.getId())
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
                .andExpect(jsonPath("$.path").value("/users/%d".formatted(USER.getId())));
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Update user with null email returns 400")
    void updateUser_withInvalidEmail_returns400() throws Exception {
        mockMvc.perform(put("/users/{userId}", USER.getId())
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
                .andExpect(jsonPath("$.path").value("/users/%d".formatted(USER.getId())));
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Update user with null or blank first name returns 400")
    void updateUser_withInvalidFirstName_returns400() throws Exception {
        for (CreateUserDto request : List.of(REGISTER_REQUEST_WITH_NULL_FIRST_NAME, REGISTER_REQUEST_WITH_BLANK_FIRST_NAME)) {
            mockMvc.perform(put("/users/{userId}", USER.getId())
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
                    .andExpect(jsonPath("$.path").value("/users/%d".formatted(USER.getId())));
            verify(userDao, times(0)).save(any());
        }
    }

    @Test
    @DisplayName("Update user with null or blank last name returns 400")
    void updateUser_withInvalidLastName_returns400() throws Exception {
        for (CreateUserDto request : List.of(REGISTER_REQUEST_WITH_NULL_LAST_NAME, REGISTER_REQUEST_WITH_BLANK_LAST_NAME)) {
            mockMvc.perform(put("/users/{userId}", USER.getId())
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
                    .andExpect(jsonPath("$.path").value("/users/%d".formatted(USER.getId())));
            verify(userDao, times(0)).save(any());
        }
    }

    @Test
    @DisplayName("Update user with null birth date returns 400")
    void updateUser_withNullBirthDate_returns400() throws Exception {
        mockMvc.perform(put("/users/{userId}", USER.getId())
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
                .andExpect(jsonPath("$.path").value("/users/%d".formatted(USER.getId())));
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Update user with age below min returns 422")
    void updateUser_withAgeBelowMin_returns422() throws Exception {
        mockMvc.perform(put("/users/{userId}", USER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(REGISTER_REQUEST_WITH_ILLEGAL_AGE))))
                .andDo(print())
                .andExpect(status().is(Error.INVALID_AGE.getHttpStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(Error.INVALID_AGE.getHttpStatus().value()))
                .andExpect(jsonPath("$.reason").value(Error.INVALID_AGE.getReason()))
                .andExpect(jsonPath("$.path").value("/users/%d".formatted(USER.getId())));
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Update user with with non-existing id throws 404")
    void updateUser_withNonExistingId_returns404() throws Exception {
        mockMvc.perform(put("/users/{userId}", NON_EXISTING_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(VALID_REGISTER_REQUEST))))
                .andDo(print())
                .andExpect(status().is(Error.USER_NOT_FOUND.getHttpStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(Error.USER_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.reason").value(Error.USER_NOT_FOUND.getReason()))
                .andExpect(jsonPath("$.path").value("/users/%d".formatted(NON_EXISTING_USER_ID)));
        verify(userDao, times(0)).save(any());
    }

    @Test
    @DisplayName("Patch user all contacts fields - OK")
    void patchUser_allContactFields_ok() throws Exception {
        mockMvc.perform(patch("/users/{userId}/contacts", USER.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeJson(DataDto.of(PATCH_USER_CONTACTS_REQUEST))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(USER.getId()))
                .andExpect(jsonPath("$.data.email").value(PATCH_USER_CONTACTS_REQUEST.getEmail()))
                .andExpect(jsonPath("$.data.firstName").value(USER.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(USER.getLastName()))
                .andExpect(jsonPath("$.data.birthDate").value(USER.getBirthDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.data.address").value(PATCH_USER_CONTACTS_REQUEST.getAddress()))
                .andExpect(jsonPath("$.data.phone").value(PATCH_USER_CONTACTS_REQUEST.getPhone()));
        final var patched = userDao.findById(USER.getId()).orElse(null);
        assertNotNull(patched);
        assertEquals(USER.getId(), patched.getId());
        assertEquals(PATCH_USER_CONTACTS_REQUEST.getEmail(), patched.getEmail());
        assertEquals(USER.getFirstName(), patched.getFirstName());
        assertEquals(USER.getLastName(), patched.getLastName());
        assertEquals(USER.getBirthDate(), patched.getBirthDate());
        assertEquals(PATCH_USER_CONTACTS_REQUEST.getAddress(), patched.getAddress());
        assertEquals(PATCH_USER_CONTACTS_REQUEST.getPhone(), patched.getPhone());
    }

    @Test
    @DisplayName("Patch user email only - OK")
    void patchUser_onlyEmail_ok() throws Exception {
        mockMvc.perform(patch("/users/{userId}/contacts", USER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(PATCH_USER_EMAIL_REQUEST))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(USER.getId()))
                .andExpect(jsonPath("$.data.email").value(PATCH_USER_EMAIL_REQUEST.getEmail()))
                .andExpect(jsonPath("$.data.firstName").value(USER.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(USER.getLastName()))
                .andExpect(jsonPath("$.data.birthDate").value(USER.getBirthDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.data.address").doesNotExist())
                .andExpect(jsonPath("$.data.phone").doesNotExist());
        final var patched = userDao.findById(USER.getId()).orElse(null);
        assertNotNull(patched);
        assertEquals(USER.getId(), patched.getId());
        assertEquals(PATCH_USER_CONTACTS_REQUEST.getEmail(), patched.getEmail());
        assertEquals(USER.getFirstName(), patched.getFirstName());
        assertEquals(USER.getLastName(), patched.getLastName());
        assertEquals(USER.getBirthDate(), patched.getBirthDate());
        assertEquals(USER.getAddress(), patched.getAddress());
        assertEquals(USER.getPhone(), patched.getPhone());
    }

    @Test
    @DisplayName("Patch user address only - OK")
    void patchUser_onlyAddress_ok() throws Exception {
        mockMvc.perform(patch("/users/{userId}/contacts", USER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(PATCH_USER_ADDRESS_REQUEST))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(USER.getId()))
                .andExpect(jsonPath("$.data.email").value(USER.getEmail()))
                .andExpect(jsonPath("$.data.firstName").value(USER.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(USER.getLastName()))
                .andExpect(jsonPath("$.data.birthDate").value(USER.getBirthDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.data.address").value(PATCH_USER_ADDRESS_REQUEST.getAddress()))
                .andExpect(jsonPath("$.data.phone").doesNotExist());
        final var patched = userDao.findById(USER.getId()).orElse(null);
        assertNotNull(patched);
        assertEquals(USER.getId(), patched.getId());
        assertEquals(USER.getEmail(), patched.getEmail());
        assertEquals(USER.getFirstName(), patched.getFirstName());
        assertEquals(USER.getLastName(), patched.getLastName());
        assertEquals(USER.getBirthDate(), patched.getBirthDate());
        assertEquals(PATCH_USER_ADDRESS_REQUEST.getAddress(), patched.getAddress());
        assertEquals(USER.getPhone(), patched.getPhone());
    }

    @Test
    @DisplayName("Patch user phone only - OK")
    void patchUser_onlyPhone_ok() throws Exception {
        mockMvc.perform(patch("/users/{userId}/contacts", USER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(PATCH_USER_PHONE_REQUEST))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.id").value(USER.getId()))
                .andExpect(jsonPath("$.data.email").value(USER.getEmail()))
                .andExpect(jsonPath("$.data.firstName").value(USER.getFirstName()))
                .andExpect(jsonPath("$.data.lastName").value(USER.getLastName()))
                .andExpect(jsonPath("$.data.birthDate").value(USER.getBirthDate().format(DateTimeFormatter.ISO_DATE)))
                .andExpect(jsonPath("$.data.address").doesNotExist())
                .andExpect(jsonPath("$.data.phone").value(PATCH_USER_PHONE_REQUEST.getPhone()));
        final var patched = userDao.findById(USER.getId()).orElse(null);
        assertNotNull(patched);
        assertEquals(USER.getId(), patched.getId());
        assertEquals(USER.getEmail(), patched.getEmail());
        assertEquals(USER.getFirstName(), patched.getFirstName());
        assertEquals(USER.getLastName(), patched.getLastName());
        assertEquals(USER.getBirthDate(), patched.getBirthDate());
        assertEquals(USER.getAddress(), patched.getAddress());
        assertEquals(PATCH_USER_PHONE_REQUEST.getPhone(), patched.getPhone());
    }

    @Test
    @DisplayName("Patch user with invalid email returns 400")
    void patchUser_withInvalidEmail_returns400() throws Exception {
        mockMvc.perform(patch("/users/{userId}/contacts", USER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(PATCH_USER_REQUEST_WITH_INVALID_EMAIL))))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad request, missing or invalid request arguments"))
                .andExpect(jsonPath("$.details").value(
                        Matchers.hasEntry("data.email", "Invalid email format")))
                .andExpect(jsonPath("$.path").value("/users/%d/contacts".formatted(USER.getId())));
        verify(userDao, times(0)).save(any());
    }


    @Test
    @DisplayName("Patch user with null data returns 400")
    void patchUser_withNullData_returns400() throws Exception {
        mockMvc.perform(patch("/users/{userId}/contacts", USER.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(new DataDto<>())))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.reason").value("Bad request, missing or invalid request arguments"))
                .andExpect(jsonPath("$.details").value(
                        Matchers.hasEntry("data", "Data must be present")))
                .andExpect(jsonPath("$.path").value("/users/%d/contacts".formatted(USER.getId())));
        verify(userDao, times(0)).save(any());
    }


    @Test
    @DisplayName("Patch user with non-existing user id returns 404")
    void patchUser_withNonExistingId_returns404() throws Exception {
        mockMvc.perform(patch("/users/{userId}/contacts", NON_EXISTING_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(DataDto.of(PATCH_USER_CONTACTS_REQUEST))))
                .andDo(print())
                .andExpect(status().is(Error.USER_NOT_FOUND.getHttpStatus().value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(Error.USER_NOT_FOUND.getHttpStatus().value()))
                .andExpect(jsonPath("$.reason").value(Error.USER_NOT_FOUND.getReason()))
                .andExpect(jsonPath("$.path").value("/users/%d/contacts".formatted(NON_EXISTING_USER_ID)));
        verify(userDao, times(0)).save(any());
    }

    private <T> T readJson(final byte[] json, TypeReference<T> typeReference) throws IOException {
        return objectMapper.readValue(json, typeReference);
    }

    private byte[] writeJson(final Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(object);
    }

    private CreateUserDto getCopyFrom(CreateUserDto validRegisterRequest) {
        final var copy = new CreateUserDto();
        BeanUtils.copyProperties(validRegisterRequest, copy);
        return copy;
    }
}