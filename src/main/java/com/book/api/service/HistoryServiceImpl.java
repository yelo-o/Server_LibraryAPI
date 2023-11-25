package com.book.api.service;

import com.book.api.dto.HistoryDto;
import com.book.api.dto.PageResponse;
import com.book.api.exceptions.ResourceNotFoundException;
import com.book.api.models.Book;
import com.book.api.models.History;
import com.book.api.repository.BookRepository;
import com.book.api.repository.HistoryRepository;
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
public class HistoryServiceImpl implements HisotryService {
    private final HistoryRepository historyRepository;
    private final BookRepository bookRepository;

    @Override
    public HistoryDto createHistory(HistoryDto historyDto) {
        History history = mapToEntity(historyDto);
        History newHistory = historyRepository.save(history);

        HistoryDto historyResponse = mapToDto(newHistory);
        return historyResponse;
    }

    @Override
    public PageResponse<?> getAllHistory(int pageNo, int pageSize) {
        Pageable pageable =
                PageRequest.of(pageNo, pageSize, Sort.by("id").descending());

        Page<History> historyPage = historyRepository.findAll(pageable);
        List<History> listOfHistory = historyPage.getContent();
        PageResponse<HistoryDto> historyResponse = mapToPageResponse(historyPage, listOfHistory);

        return historyResponse;
    }





    //히스토리 id에 대한 것을 찾는 경우는 없을 것 같고.. Book 자체에서 골라줘야 할듯?
    @Override
    public HistoryDto getHistoryById(Long id) {
        History history = getExistHistory(id);
        return mapToDto(history);
    }



    private History getExistHistory(Long id) {
        return historyRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("History could not be found"));
    }

    private Book getExistBook(Long id) {
        return bookRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Book could not be found"));
    }


    //Mapping Methods 1
    private HistoryDto mapToDto(History history) {
        return HistoryDto.builder()
                .id(history.getId())
                .build();
    }

    //Mapping Methods 2
    private History mapToEntity(HistoryDto historyDto) {
        return History.builder()
                .build();
    }



    //Mapping Methods 3
    private PageResponse<HistoryDto> mapToPageResponse(Page<History> historyPage,
                                                       List<History> listOfHistory) {
        List<HistoryDto> content =
                listOfHistory.stream()
                        .map(this::mapToDto)
                        .collect(Collectors.toList());

        PageResponse<HistoryDto> historyResponse = new PageResponse<>();
        historyResponse.setContent(content);
        historyResponse.setPageNo(historyPage.getNumber());
        historyResponse.setPageSize(historyPage.getSize());
        historyResponse.setTotalElements(historyPage.getTotalElements());
        historyResponse.setTotalPages(historyPage.getTotalPages());
        historyResponse.setLast(historyPage.isLast());

        return historyResponse;
    }

}
