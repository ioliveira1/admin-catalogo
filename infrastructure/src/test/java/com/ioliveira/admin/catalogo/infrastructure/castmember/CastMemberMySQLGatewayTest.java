package com.ioliveira.admin.catalogo.infrastructure.castmember;

import com.ioliveira.admin.catalogo.Fixture;
import com.ioliveira.admin.catalogo.MySQLGatewayTest;
import com.ioliveira.admin.catalogo.domain.castmember.CastMember;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberID;
import com.ioliveira.admin.catalogo.domain.castmember.CastMemberType;
import com.ioliveira.admin.catalogo.domain.pagination.SearchQuery;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberJpaEntity;
import com.ioliveira.admin.catalogo.infrastructure.castmember.persistence.CastMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@MySQLGatewayTest
public class CastMemberMySQLGatewayTest {

    @Autowired
    private CastMemberMySQLGateway castMemberGateway;

    @Autowired
    private CastMemberRepository castMemberRepository;

    @BeforeEach
    void cleanUp() {
        this.castMemberRepository.deleteAll();
    }

    @Test
    public void givenAValidCastMember_whenCallsCreate_shouldPersistIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();

        assertEquals(0, castMemberRepository.count());

        final var actualMember = castMemberGateway.create(CastMember.clone(member));

        assertEquals(1, castMemberRepository.count());

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(member.getCreatedAt(), actualMember.getCreatedAt());
        assertEquals(member.getUpdatedAt(), actualMember.getUpdatedAt());

        final var persistedMember = castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), persistedMember.getId());
        assertEquals(expectedName, persistedMember.getName());
        assertEquals(expectedType, persistedMember.getType());
        assertEquals(member.getCreatedAt(), persistedMember.getCreatedAt());
        assertEquals(member.getUpdatedAt(), persistedMember.getUpdatedAt());
    }

    @Test
    public void givenAValidCastMember_whenCallsUpdate_shouldRefreshIt() {
        final var expectedName = Fixture.name();
        final var expectedType = CastMemberType.ACTOR;

        final var member = CastMember.newMember("vind", CastMemberType.DIRECTOR);
        final var expectedId = member.getId();

        final var currentMember = castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, castMemberRepository.count());
        assertEquals("vind", currentMember.getName());
        assertEquals(CastMemberType.DIRECTOR, currentMember.getType());

        final var actualMember = castMemberGateway.update(
                CastMember.clone(member).update(expectedName, expectedType)
        );

        assertEquals(1, castMemberRepository.count());

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(member.getCreatedAt(), actualMember.getCreatedAt());
        assertTrue(member.getUpdatedAt().isBefore(actualMember.getUpdatedAt()));

        final var persistedMember = castMemberRepository.findById(expectedId.getValue()).get();

        assertEquals(expectedId.getValue(), persistedMember.getId());
        assertEquals(expectedName, persistedMember.getName());
        assertEquals(expectedType, persistedMember.getType());
        assertEquals(member.getCreatedAt(), persistedMember.getCreatedAt());
        assertTrue(member.getUpdatedAt().isBefore(persistedMember.getUpdatedAt()));
    }

    @Test
    public void givenAValidCastMember_whenCallsDeleteById_shouldDeleteIt() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, castMemberRepository.count());

        castMemberGateway.deleteById(member.getId());

        assertEquals(0, castMemberRepository.count());
    }

    @Test
    public void givenAnInvalidId_whenCallsDeleteById_shouldBeIgnored() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, castMemberRepository.count());

        castMemberGateway.deleteById(CastMemberID.from("123"));

        assertEquals(1, castMemberRepository.count());
    }

    @Test
    public void givenAValidCastMember_whenCallsFindById_shouldReturnIt() {
        final var expectedName = Fixture.name();
        final var expectedType = Fixture.CastMember.type();

        final var member = CastMember.newMember(expectedName, expectedType);
        final var expectedId = member.getId();

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, castMemberRepository.count());

        final var actualMember = castMemberGateway.findById(expectedId).get();

        assertEquals(expectedId, actualMember.getId());
        assertEquals(expectedName, actualMember.getName());
        assertEquals(expectedType, actualMember.getType());
        assertEquals(member.getCreatedAt(), actualMember.getCreatedAt());
        assertEquals(member.getUpdatedAt(), actualMember.getUpdatedAt());
    }

    @Test
    public void givenAnInvalidId_whenCallsFindById_shouldReturnEmpty() {
        final var member = CastMember.newMember(Fixture.name(), Fixture.CastMember.type());

        castMemberRepository.saveAndFlush(CastMemberJpaEntity.from(member));

        assertEquals(1, castMemberRepository.count());

        final var actualMember = castMemberGateway.findById(CastMemberID.from("123"));

        assertTrue(actualMember.isEmpty());
    }

    @Test
    public void givenEmptyCastMembers_whenCallsFindAll_shouldReturnEmpty() {
        final var expectedPage = 0;
        final var expectedPerPage = 10;
        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";
        final var expectedTotal = 0;

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualPage = castMemberGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedTotal, actualPage.items().size());
    }

    @ParameterizedTest
    @CsvSource({
            "vin,0,10,1,1,Vin Diesel",
            "taran,0,10,1,1,Quentin Tarantino",
            "jas,0,10,1,1,Jason Momoa",
            "har,0,10,1,1,Kit Harington",
            "MAR,0,10,1,1,Martin Scorsese",
    })
    public void givenAValidTerm_whenCallsFindAll_shouldReturnFiltered(
            final String expectedTerms,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        mockMembers();

        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualPage = castMemberGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "name,asc,0,10,5,5,Jason Momoa",
            "name,desc,0,10,5,5,Vin Diesel",
            "createdAt,asc,0,10,5,5,Kit Harington",
            "createdAt,desc,0,10,5,5,Martin Scorsese",
    })
    public void givenAValidSortAndDirection_whenCallsFindAll_shouldReturnSorted(
            final String expectedSort,
            final String expectedDirection,
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedName
    ) {
        mockMembers();

        final var expectedTerms = "";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualPage = castMemberGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());
        assertEquals(expectedName, actualPage.items().get(0).getName());
    }

    @ParameterizedTest
    @CsvSource({
            "0,2,2,5,Jason Momoa;Kit Harington",
            "1,2,2,5,Martin Scorsese;Quentin Tarantino",
            "2,2,1,5,Vin Diesel",
    })
    public void givenAValidPagination_whenCallsFindAll_shouldReturnPaginated(
            final int expectedPage,
            final int expectedPerPage,
            final int expectedItemsCount,
            final long expectedTotal,
            final String expectedNames
    ) {
        mockMembers();

        final var expectedTerms = "";
        final var expectedSort = "name";
        final var expectedDirection = "asc";

        final var query =
                new SearchQuery(expectedPage, expectedPerPage, expectedTerms, expectedSort, expectedDirection);

        final var actualPage = castMemberGateway.findAll(query);

        assertEquals(expectedPage, actualPage.currentPage());
        assertEquals(expectedPerPage, actualPage.perPage());
        assertEquals(expectedTotal, actualPage.total());
        assertEquals(expectedItemsCount, actualPage.items().size());

        int index = 0;
        for (final var expectedName : expectedNames.split(";")) {
            assertEquals(expectedName, actualPage.items().get(index).getName());
            index++;
        }
    }

    private void mockMembers() {
        castMemberRepository.saveAllAndFlush(List.of(
                CastMemberJpaEntity.from(CastMember.newMember("Kit Harington", CastMemberType.ACTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Vin Diesel", CastMemberType.ACTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Quentin Tarantino", CastMemberType.DIRECTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Jason Momoa", CastMemberType.ACTOR)),
                CastMemberJpaEntity.from(CastMember.newMember("Martin Scorsese", CastMemberType.DIRECTOR))
        ));
    }

}