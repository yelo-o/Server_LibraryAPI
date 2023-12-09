package com.book.api.controllers;

import com.book.api.dto.BookDto;
import com.book.api.dto.PageResponse;
import com.book.api.jwt.JWTGenerator;
import com.book.api.models.BookStatus;
import com.book.api.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTGenerator jwtGenerator;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(springSecurity())
                .build();
    }

    @DisplayName("모든 책을 불러온다.")
    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllBooksTest() throws Exception {
        BookDto bookDto = BookDto.builder()
                .id(1L)
                .title("자바의 정석")
                .author("남궁성")
                .publisher("도우 출판")
                .status(BookStatus.AVAILABLE)
                .quantity(10)
                .build();

        PageResponse responseDto =
                PageResponse.builder()
                        .pageSize(10)
                        .last(true)
                        .pageNo(1)
                        .content(Arrays.asList(bookDto))
                        .build();

        when(bookService.getAllBook(1,10))
                .thenReturn(responseDto);

        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("pageNo","1")
                        .param("pageSize","10"))
                .andExpect(status().isOk())
                .andDo(document("get-all-books",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
//                        pathParameters(
//                                parameterWithName("pageNo").description("현재 페이지"),
//                                parameterWithName("pageSize").description("개수")
//                        ),
                        responseFields(
                                fieldWithPath("content[0].id").description("id"),
                                fieldWithPath("content[0].title").description("제목"),
                                fieldWithPath("content[0].author").description("작가"),
                                fieldWithPath("content[0].publisher").description("출판사"),
                                fieldWithPath("content[0].quantity").description("수량"),
                                fieldWithPath("content[0].status").description("책 상태"),
                                fieldWithPath("pageNo").description("페이지 번호"),
                                fieldWithPath("pageSize").description("한 페이지에 보이는 개수"),
                                fieldWithPath("totalElements").description("총 개수"),
                                fieldWithPath("totalPages").description("총 페이지"),
                                fieldWithPath("last").description("마지막 페이지 여부"))))
//                        requestFields(
//                                fieldWithPath("id").description("id"),
//                                fieldWithPath("title").description("제목"),
//                                fieldWithPath("author").description("작가"),
//                                fieldWithPath("publisher").description("출판사"),
//                                fieldWithPath("quantity").description("수량"),
//                                fieldWithPath("status").description("책 상태"))))
                .andDo(print())
                .andReturn();

    }

    @DisplayName("책을 한 권 조회한다.")
    @Test
    @WithMockUser(roles = "USER")
    public void getBookTest() throws Exception {
        //given
        BookDto bookDto = BookDto.builder()
                .id(1L)
                .title("자바의 정석")
                .author("남궁성")
                .publisher("도우 출판")
                .status(BookStatus.AVAILABLE)
                .quantity(10)
                .build();

        //when
        Long bookId = 1L;
        when(bookService.getBookById(bookId))
                .thenReturn(bookDto);


        //then
        MvcResult result = mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/api/book/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("get-book",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        pathParameters(parameterWithName("id").description("책 ID")),
                        responseFields(
                                fieldWithPath("id").description("id"),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("author").description("작가"),
                                fieldWithPath("publisher").description("출판사"),
                                fieldWithPath("quantity").description("수량"),
                                fieldWithPath("status").description("책 상태"))))
                .andDo(print())
                .andReturn();
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response Content: " + responseContent);
    }

    @DisplayName("책을 등록한다.")
    @Test
    @WithMockUser(roles = "ADMIN")
    public void saveBookTest() throws Exception {
        //given
        given(bookService.createBook(ArgumentMatchers.any()))
                .willAnswer((invocation -> invocation.getArgument(0)));

        Long bookId = 1L;
        BookDto bookDto = BookDto.builder()
                .id(bookId)
                .title("자바의 정석")
                .author("남궁성")
                .publisher("도우 출판")
                .status(BookStatus.AVAILABLE)
                .quantity(10)
                .build();

        //when
        when(bookService.getBookById(bookId))
                .thenReturn(bookDto);

        //then
        mockMvc.perform(post("/api/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto))) // Request body
//                            .andExpect(content().json(objectMapper.writeValueAsString(bookDto)))
                            .andExpect(status().isCreated())
                .andDo(document("create-book",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseFields(
                                fieldWithPath("id").description("id"),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("author").description("작가"),
                                fieldWithPath("publisher").description("출판사"),
                                fieldWithPath("quantity").description("수량"),
//                                fieldWithPath("status").description("책 상태")),
                                fieldWithPath("status").description("책 상태"))))
//                        requestFields(
//                                fieldWithPath("id").description("id"),
//                                fieldWithPath("title").description("제목"),
//                                fieldWithPath("author").description("작가"),
//                                fieldWithPath("publisher").description("출판사"),
//                                fieldWithPath("quantity").description("수량"),
//                                fieldWithPath("status").description("책 상태"))))
                .andDo(print())
                .andReturn();

    }





}