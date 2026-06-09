# Guia de Configuração do Ambiente Java + Maven + IntelliJ

---

##  Instalar o JDK 17

**Versão recomendada:** `Java 17.0.12 LTS`  

### 1. Acesse um dos sites oficiais para baixar o JDK:   (Java SE Development Kit)
   - [Oracle JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)  


### 2. Baixe a versão para o seu sistema operacional (Windows, macOS ou Linux) e siga as instruções do instalador.  

### 3. Configure a variável de ambiente `JAVA_HOME`:  
   - **Windows:**  
     1. Pesquise “Variáveis de Ambiente” → **Editar variáveis do sistema**  
     2. Clique em **Nova…** e crie `JAVA_HOME` apontando para o diretório do JDK (ex: `C:\Program Files\Java\jdk-17`)  
     3. Adicione `%JAVA_HOME%\bin` no Path 
   - **macOS/Linux:**  
     ```bash
     export JAVA_HOME=/caminho/para/jdk-17
     export PATH=$JAVA_HOME/bin:$PATH
     ```

### 4. Teste a instalação:  
```bash
java -version
Deve aparecer:

java version "17.0.12" 2024-07-16 LTS

```
### 5. Instalar o Maven
Versão recomendada: Maven 3.9.11

Baixe o Maven no site oficial: https://maven.apache.org/download.cgi

Escolha o Binary zip archive para o seu sistema operacional.

Extraia o arquivo baixado para uma pasta no seu computador, por exemplo:

C:\Program Files\Maven
Configure a variável de ambiente MAVEN_HOME:
Windows:

MAVEN_HOME = C:\Program Files\Maven\apache-maven-3.9.11
Adicione %MAVEN_HOME%\bin ao Path


### 6. Instalar o IntelliJ IDEA
Baixe o IntelliJ IDEA: https://www.jetbrains.com/idea/download/

Escolha a Community Edition (gratuita) ou Ultimate (paga).

Siga as instruções do instalador para finalizar a instalação.

# 🔄 Atualizar branch com a main antes do Pull Request (PR)
Quando a branch `main` foi atualizada enquanto você ainda estava trabalhando em uma outra branch, o procedimento recomendado é seguir os passos abaixo para garantir que sua branch esteja sincronizada com as últimas alterações da `main`:

1 - **Atualize a branch `main`**: Antes de fazer qualquer coisa, volte para a branch `main` e garanta que ela tenha todas as atualizações mais recentes:
- `git checkout main` 
- `git pull origin main` (ou só `git pull`)

2 - **Mescle a branch `main` na branch que está trabalhando**: Agora, volte para a branch que está trabalhando e faça a mesclagem da branch `main` para trazer as atualizações:

- `git checkout {nome da branch que estava trabalhando}`
- `git merge main`

Isso vai mesclar as mudanças da `main` para a branch que está trabalhando. Caso existam conflitos, o Git vai alertar, e você poderá resolvê-los manualmente.

3 - **Resolva conflitos (se houver)**: Se surgirem conflitos durante a mesclagem, o Git indicará os arquivos conflitantes. Você precisará editar esses arquivos, resolver os conflitos e, em seguida, fazer um commit:
- `git add <arquivo_resolvido>` 
- `git commit`



