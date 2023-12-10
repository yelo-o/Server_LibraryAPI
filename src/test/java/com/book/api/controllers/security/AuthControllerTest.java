package com.book.api.controllers.security;

import com.book.api.dto.security.AuthResponseDto;
import com.book.api.dto.security.UserDto;
import com.book.api.jwt.JWTGenerator;
import com.book.api.models.security.RoleEntity;
import com.book.api.models.security.UserEntity;
import com.book.api.repository.security.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@ExtendWith(RestDocumentationExtension.class)
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
//@AutoConfigureMockMvc()
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
//    private UserEntity userEntity;
//    private UserDto userdto;
    @Autowired
    private  PasswordEncoder passwordEncoder;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private RoleRepository roleRepository;


    @Autowired
    public AuthControllerTest(WebApplicationContext context) {
        this.context = context;
    }

    WebApplicationContext context;
    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                    .operationPreprocessors()
                    .withRequestDefaults(modifyUris().host("localhost").removePort(), prettyPrint())
                    .withResponseDefaults(modifyUris().host("localhost").removePort(), prettyPrint()))
//                .defaultRequest(get("/api/auth").with(user("boot2").roles("USER"))) //북 컨트롤러에서 사용하면 좋을 듯?
                .apply(springSecurity())
                .build();
    }

    @DisplayName("웰컴 테스트")
    @Test
    public void welcome() throws Exception {
        String message = "Welcome this endpoint is not secure";

        mockMvc.perform(get("/api/auth/welcome"))
                .andExpect(status().isOk())
                .andExpect(content().string(message))
                .andDo(document("welcome"))
                .andDo(print());
    }

    @DisplayName("가입 테스트")
    @Test
    void register() throws Exception {

        UserDto userDto = UserDto.builder()
                .username("boot")
                .password("123456")
                .firstName("Tom")
                .lastName("Kim")
                .role("ROLE_USER")
                .build();

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode((userDto.getPassword())));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        RoleEntity roles = roleRepository.findByName(userDto.getRole())
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName(userDto.getRole());
                    return roleRepository.save(role);
                });
        user.setRoles(Collections.singletonList(roles));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("유저가 등록되었습니다."))
                .andDo(document("create-user",
//                        Preprocessors.preprocessRequest(prettyPrint()),
//                        Preprocessors.preprocessResponse(prettyPrint()),
                        requestFields(
                            fieldWithPath("id").description("id"),
                            fieldWithPath("username").description("아이디"),
                            fieldWithPath("password").description("비밀번호"),
                            fieldWithPath("firstName").description("이름"),
                            fieldWithPath("lastName").description("성"),
                            fieldWithPath("roles").description("역할"),
                            fieldWithPath("roles[0].id").description("역할 id"),
                            fieldWithPath("roles[0].name").description("역할명"))))
                .andDo(print())
                .andReturn();

    }
}