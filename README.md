## ▶️ Passo a passo para executar o backend (local)

### Requisitos mínimos
- `Java 21` (ver `pom.xml`)
- `Maven`
- `PostgreSQL`

### 1) Clonar o repositório

```bash
git clone https://github.com/HenriquePrestes/backend-repo
cd backend-repo
```

### 2) Criar o banco de dados
O profile `local` usa por padrão `clinica_db_local`. Crie o banco no PostgreSQL:

```bash
# como usuário postgres
createdb -U postgres clinica_db_local
# ou via psql
psql -U postgres -c "CREATE DATABASE clinica_db_local;"
```

### 3) Configurar variáveis de ambiente
Copie `.env.example` para `.env` e preencha as variáveis necessárias (ex.: `BD_DATASOURCE_PASSWORD`, `SPRING_PROFILES_ACTIVE=local`, `SERVER_PORT`).
Verifique também `src/main/resources/application-local.properties` para confirmar `spring.datasource.*`.

### 4) Executar a aplicação
Opção A — rodar direto com Maven:

```bash
mvn spring-boot:run
```

Opção B — empacotar e executar o jar:

```bash
mvn clean package
java -jar target/*.jar
```

### Observações sobre o schema do banco
- O projeto utiliza JPA/Hibernate com `spring.jpa.hibernate.ddl-auto=update` (veja `application-local.properties`), então as tabelas são criadas/atualizadas automaticamente a partir das entidades.
- Não há script SQL versionado no repositório. Se o avaliador solicitar um script, gere um dump do banco usado na demonstração e inclua `dump.sql` (ex.: `pg_dump -U postgres -d clinica_db_local > dump.sql`).

### Arquivos recomendados para incluir na entrega (ZIP/RAR)

- Código-fonte do backend (`src/`, `pom.xml`, `.env.example`)
- Código-fonte do frontend (`frontend/`, `package.json`, `.env.example`)
- Arquivo com orientações de execução (README atualizados)
- Script/dump do banco (`dump.sql`), se solicitado
- Link de acesso à aplicação publicada (se houver)

---

Se quiser, eu posso adicionar comentários ao arquivo `.env.example` ou gerar um modelo de `dump.sql` a partir do seu banco local.



