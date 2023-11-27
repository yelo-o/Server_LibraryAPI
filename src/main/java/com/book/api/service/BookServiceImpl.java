package com.book.api.service;

import com.book.api.dto.BookDto;
import com.book.api.dto.PageResponse;
import com.book.api.exceptions.ResourceNotFoundException;
import com.book.api.models.Book;
import com.book.api.models.BookStatus;
import com.book.api.models.History;
import com.book.api.models.HistoryType;
import com.book.api.models.security.UserEntity;
import com.book.api.repository.BookRepository;
import com.book.api.repository.HistoryRepository;
import com.book.api.repository.security.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService{
    private final BookRepository bookRepository;
    private final HistoryRepository historyRepository;
    private final UserRepository userRepository;

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = mapToEntity(bookDto);
        Book newBook = bookRepository.save(book);

        BookDto bookResponse = mapToDto(newBook);

        return bookResponse;
    }

    @Override
    public boolean createBorrowHistory(Long bookId, String username) {
        //Get User Entity
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        UserEntity user = optionalUser.orElseThrow();

        Book book = getExistBook(bookId);
//        BookStatus status = book.getStatus();
        int quantity = book.getQuantity();
        //이미 빌린 책인지 확인
        Optional<History> historyByUserAndBookOptional = historyRepository.findFirstHistoryByUserAndBookOrderByCreatedDateDesc(user, book);
        History historyByUserAndBook = historyByUserAndBookOptional.orElse(History.builder().build());
        historyRepository.save(historyByUserAndBook);

        if (historyByUserAndBook.getType() == HistoryType.BORROW) {
            System.out.println("이미 빌린 책입니다.");
            return false;
        }

        if (quantity > 0) {
            //historyByUserAndBook 업데이트
            historyByUserAndBook.setUser(user);
            historyByUserAndBook.setBook(book);
            historyByUserAndBook.setType(HistoryType.BORROW);
            //historyRepository.save(historyByUserAndBook); -> Dirty checking

            //Book update
            quantity--;
            book.setQuantity(quantity);
            if (quantity == 0) {
                book.setStatus(BookStatus.VACANT);
            }
            bookRepository.save(book);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean createReturnHistory(Long bookId, String username) {
        //Get User Entity
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        UserEntity user = optionalUser.orElseThrow();

        Book book = getExistBook(bookId);
        BookStatus status = book.getStatus();
        int quantity = book.getQuantity();
        quantity++;
        book.setQuantity(quantity);


        //이 사용자가 책을 빌렸었는지도 확인해야 함
        Optional<History> historyByUserAndBookOptional = historyRepository.findFirstHistoryByUserAndBookOrderByCreatedDateDesc(user, book);
        History historyByUserAndBook = historyByUserAndBookOptional.orElseThrow();
        System.out.println("히스토리 번호 : "+ historyByUserAndBook.getId());
        System.out.println("책 번호 : "+ historyByUserAndBook.getBook().getId());
        System.out.println("빌림/반납 : "+ historyByUserAndBook.getType());
        System.out.println("히스토리 리스트");
        System.out.println(historyByUserAndBook.getBook().getHistories());

        if (historyByUserAndBook.getType() == HistoryType.RETURN) {
            System.out.println("이미 반납한 책입니다.");
            return false;
        }

        if (status == BookStatus.VACANT) {
            book.setStatus(BookStatus.AVAILABLE);
        }
        bookRepository.save(book);

        History history = History.builder()
                .user(user)
                .book(book)
                .type(HistoryType.RETURN)
                .build();
        historyRepository.save(history);
        return true;
    }

    @Override
    public PageResponse<?> getAllBook(int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

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
        if (bookDto.getTitle() != null) {
            book.setTitle(bookDto.getTitle());
        }
//        if (bookDto.getType() != null) {
//            book.setType(bookDto.getType());
//        }
        return mapToDto(book);
    }

    @Override
    public boolean updateHistoryById(Long id) {
        //user id
        //book id
        //history id
        History history = getExistHistory(id);
        Book book = getExistBook(id);
        if (book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.VACANT);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteBookById(Long id) {
        Book book = getExistBook(id);
        bookRepository.delete(book);
    }


    private Book getExistBook(Long id) {
        return bookRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book could not be found"));
    }

    private History getExistHistory(Long id) {
        return historyRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("History could not be found"));
    }

    //Dto 클래스로 변환
    private BookDto mapToDto(Book book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .status(book.getStatus())
                .build();
    }

    //Entity 클래스로 변환
    private Book mapToEntity(BookDto bookDto) {
        return Book.builder()
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .publisher(bookDto.getPublisher())
                .status(bookDto.getStatus())
                .build();
    }

}
