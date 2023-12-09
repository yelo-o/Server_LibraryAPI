package com.book.api.controllers.security;

import com.book.api.config.security.SecurityConfig;
import com.book.api.dto.security.AuthResponseDto;
import com.book.api.dto.security.LoginDto;
import com.book.api.dto.security.UserDto;
import com.book.api.jwt.JWTGenerator;
import com.book.api.models.security.RoleEntity;
import com.book.api.models.security.UserEntity;
import com.book.api.repository.security.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.ManualRestDocumentation;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
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
                .apply(documentationConfiguration(restDocumentation))
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
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
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

    /*
    @DisplayName("로그인 테스트")
    @Test
    void login() throws Exception {

        //회원가입 코드
        UserDto userDto = UserDto.builder()
                .username("boot2")
                .password("123456")
                .firstName("Tom2")
                .lastName("Kim2")
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
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("id").description("id"),
                                fieldWithPath("username").description("아이디"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("firstName").description("이름"),
                                fieldWithPath("lastName").description("성"),
                                fieldWithPath("roles").description("역할"),
                                fieldWithPath("roles[0].id").description("역할 id"),
                                fieldWithPath("roles[0].name").description("역할명"))))
                .andDo(print());


        //여기부터 로그인
        //Authentication
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userDto.getUsername(),
                        userDto.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        //generate token
        String token = jwtGenerator.generateToken(authentication);

        AuthResponseDto authResponseDto = new AuthResponseDto(token);
        authResponseDto.setUsername(userDto.getUsername());
//        authResponseDto.setTokenType("Bearer ");
//        authResponseDto.setToken(token);
//        authResponseDto.setRole("ROLE_USER");

        mockMvc.perform(post("/api/auth/login")
//                        .header("Authorization","Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(authResponseDto)))
                .andDo(document("user-login",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
//                                fieldWithPath("id").description("id"),
                                fieldWithPath("username").description("아이디"),
                                fieldWithPath("password").description("비밀번호"))))
                .andDo(print())
                .andReturn();
    }
    */
}