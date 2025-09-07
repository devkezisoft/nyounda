package com.kezisoft.nyounda.api.it;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kezisoft.nyounda.application.auth.port.out.PinCodeProvider;
import com.kezisoft.nyounda.domain.user.User;
import com.kezisoft.nyounda.domain.user.UserRole;
import com.kezisoft.nyounda.persistence.categories.entity.CategoryEntity;
import com.kezisoft.nyounda.persistence.user.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public abstract class AbstractIntegrationTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected EntityManager em;
    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    PinCodeProvider pinCodeProvider;

    //✅ JVM-wide singleton container (no @BeforeAll, no @Testcontainers needed)
    private static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("nyounda")
                .withUsername("nyounda")
                .withPassword("nyounda")
                .withReuse(true); // optional; see note below
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }

    private User insertUser(UUID id, String fullName, String email, String phone, List<String> roles) {
        var user = UserEntity.builder()
                .id(id).fullName(fullName).email(email).phone(phone)
                .avatarUrl(null).roles(roles).build();
        em.persist(user);
        em.flush();
        return user.toDomain();
    }

    /**
     * Returns [rootId, subId] with sub.parent = root
     */
    protected UUID[] seedCategoryHierarchy(String rootName, String subName) {
        CategoryEntity root = CategoryEntity.builder()
                .name(rootName).emoji("🧰").description("Root " + rootName)
                .build();
        em.persist(root);

        CategoryEntity sub = CategoryEntity.builder()
                .name(subName).emoji("🔩").description("Sub " + subName).parent(root)
                .build();
        em.persist(sub);

        em.flush();
        return new UUID[]{root.getId(), sub.getId()};
    }

    protected User seedUserClient(String fullName, String email, String phone) {
        return insertUser(UUID.randomUUID(), fullName, email, phone, List.of(UserRole.CLIENT.name()));
    }

    protected User seedUserProvider(String fullName, String email, String phone) {
        return insertUser(UUID.randomUUID(), fullName, email, phone, List.of(UserRole.PROVIDER.name()));
    }
}
