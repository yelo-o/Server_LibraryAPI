package com.book.api.service;

import com.book.api.dto.BookDto;
import com.book.api.dto.PageResponse;
import com.book.api.exceptions.ResourceNotFoundException;
import com.book.api.models.Book;
import com.book.api.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService{
    private final BookRepository bookRepository;

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = mapToEntity(bookDto);

        Book newBook = bookRepository.save(book);

        BookDto bookResponse = new BookDto();
        bookResponse.setId(newBook.getId());
        bookResponse.setName(newBook.getName());
        bookResponse.setType(newBook.getType());
        return bookResponse;
    }

    @Override
    public PageResponse<?> getAllBook(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<Book> bookPage = bookRepository.findAll(pageable);
        List<Book> listOfBook = bookPage.getContent();
        List<BookDto> content = listOfBook.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        PageResponse<BookDto> bookResponse = new PageResponse<>();
        bookResponse.setContent(content);
        bookResponse.setPageNo(bookPage.getNumber()); //페이지 번호
        bookResponse.setPageSize(bookPage.getSize()); //페이지 사이즈
        bookResponse.setTotalElements(bookPage.getTotalElements()); //전체 엘리멘트 개수
        bookResponse.setTotalPages(bookPage.getTotalPages()); //전체 엘리멘트 개수/페이지 사이즈
        bookResponse.setLast(bookPage.isLast());

        return bookResponse;
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = getExistBook(id);
        return mapToDto(book);
    }

    @Override
    public BookDto updateBookById(BookDto bookDto, Long id) {
        Book book = getExistBook(id);
        if (bookDto.getName() != null) {
            book.setName(bookDto.getName());
        }
        if (bookDto.getType() != null) {
            book.setType(bookDto.getType());
        }
        return mapToDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        Book book = getExistBook(id);
        bookRepository.delete(book);
    }


    //중복 확인
    private Book getExistBook(Long id) {
        return bookRepository
                .findById(id) //Optional<Pokemon>
                .orElseThrow(() ->
                        new ResourceNotFoundException("Pokemon could not be found"));
    }

    //Dto 클래스로 변환
    private BookDto mapToDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setName(book.getName());
        bookDto.setType(book.getType());
        return bookDto;
    }

    //Entity 클래스로 변환
    private Book mapToEntity(BookDto bookDto) {
        Book book = new Book();
        book.setName(bookDto.getName());
        book.setType(bookDto.getType());
        book.setType(bookDto.getType());
        return book;
    }

}
