-- Required by Spring AI's PgVectorStore (spring.ai.vectorstore.pgvector.initialize-schema=true
-- will create the vector_store table itself, but the extension must exist first).
CREATE EXTENSION IF NOT EXISTS vector;
