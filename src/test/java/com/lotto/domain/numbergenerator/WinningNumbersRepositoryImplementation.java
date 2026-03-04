package com.lotto.domain.numbergenerator;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

class WinningNumbersRepositoryImplementation implements WinningNumbersRepository {

    private final List<WinningNumbers> winningNumbersList = new ArrayList<>();

    @Override
    public WinningNumbers save(final WinningNumbers winningNumbers) {
        winningNumbersList.add(winningNumbers);
        return winningNumbers;
    }

    @Override
    public List<WinningNumbers> findAll() {
        return winningNumbersList;
    }

    @Override
    public Optional<WinningNumbers> findWinningNumbersByDate(final Instant drawDate) {
        return winningNumbersList.stream()
                .filter(w -> w.date().equals(drawDate))
                .findFirst();
    }

    @Override
    public <S extends WinningNumbers, R> R findBy(final Example<S> example, final Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends WinningNumbers> boolean exists(final Example<S> example) {
        return false;
    }

    @Override
    public <S extends WinningNumbers> long count(final Example<S> example) {
        return 0;
    }

    @Override
    public <S extends WinningNumbers> Page<S> findAll(final Example<S> example, final Pageable pageable) {
        return null;
    }

    @Override
    public <S extends WinningNumbers> Optional<S> findOne(final Example<S> example) {
        return Optional.empty();
    }

    @Override
    public Page<WinningNumbers> findAll(final Pageable pageable) {
        return null;
    }

    @Override
    public List<WinningNumbers> findAll(final Sort sort) {
        return List.of();
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void deleteAll(final Iterable<? extends WinningNumbers> entities) {

    }

    @Override
    public void deleteAllById(final Iterable<? extends String> strings) {

    }

    @Override
    public void delete(final WinningNumbers entity) {

    }

    @Override
    public void deleteById(final String s) {

    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public boolean existsById(final String s) {
        return false;
    }

    @Override
    public Optional<WinningNumbers> findById(final String s) {
        return Optional.empty();
    }

    @Override
    public List<WinningNumbers> findAllById(final Iterable<String> strings) {
        return List.of();
    }

    @Override
    public <S extends WinningNumbers> List<S> saveAll(final Iterable<S> entities) {
        return List.of();
    }

    @Override
    public <S extends WinningNumbers> List<S> findAll(final Example<S> example, final Sort sort) {
        return List.of();
    }

    @Override
    public <S extends WinningNumbers> List<S> findAll(final Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends WinningNumbers> List<S> insert(final Iterable<S> entities) {
        return List.of();
    }

    @Override
    public <S extends WinningNumbers> S insert(final S entity) {
        return null;
    }
}
