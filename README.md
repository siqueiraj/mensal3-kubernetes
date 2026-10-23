# Siqueira Lanches — Mensal 3: Kubernetes

Projeto acadêmico de implantação da aplicação **Siqueira Lanches** em um ambiente Kubernetes local utilizando o Minikube.

## Integrantes

- João Victor Siqueira
- Lucas Eduardo Schneider
- Carlos Feliponi

## Tecnologias utilizadas

- **Frontend:** Angular, servido pelo Nginx
- **Backend:** Java 17, Spring Boot e Maven
- **Banco de dados:** MySQL 8.0
- **Contêineres:** Docker
- **Orquestração:** Kubernetes com Minikube

## Estrutura do repositório

```text
mensal3-kubernetes/
├── evidencias/
├── k8s/
│   ├── backend.yaml
│   ├── frontend.yaml
│   └── mysql.yaml
├── siqueira-lanches-backend/
│   ├── src/
│   ├── Dockerfile
│   └── pom.xml
├── siqueiraLanches/
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
├── .gitignore
└── README.md
```

As pastas do frontend e backend contêm seus respectivos códigos-fonte e arquivos de configuração.

## Pré-requisitos

Para executar o projeto localmente, é necessário ter instalado:

- Docker Desktop configurado para contêineres Linux
- Minikube
- kubectl
- Git

## Como executar no Minikube

Abra o PowerShell na pasta principal do repositório.

### 1. Iniciar o Minikube

```powershell
minikube start --driver=docker
minikube status
```

Verifique se os componentes do Minikube estão em execução.

### 2. Construir as imagens Docker

Como os Deployments utilizam `imagePullPolicy: Never`, as imagens precisam estar disponíveis no ambiente do Minikube.

**Backend:**

```powershell
minikube image build -t siqueira-backend:1.0 .\siqueira-lanches-backend
```

**Frontend:**

```powershell
minikube image build -t siqueira-frontend:1.2 .\siqueiraLanches
```

### 3. Implantar os componentes no Kubernetes

Primeiro, implante o banco de dados MySQL:

```powershell
kubectl apply -f .\k8s\mysql.yaml
kubectl rollout status deployment/mysql --timeout=180s
```

Depois, implante o backend:

```powershell
kubectl apply -f .\k8s\backend.yaml
kubectl rollout status deployment/backend --timeout=180s
```

Por último, implante o frontend:

```powershell
kubectl apply -f .\k8s\frontend.yaml
kubectl rollout status deployment/frontend --timeout=180s
```

### 4. Verificar os recursos

Execute os comandos abaixo para verificar os Pods, Services, Deployments e volumes persistentes:

```powershell
kubectl get pods -o wide
kubectl get services
kubectl get deployments
kubectl get pvc
```

O ambiente deve apresentar:

- 1 Pod do frontend
- 2 Pods do backend
- 1 Pod do MySQL
- Services responsáveis pela comunicação
- Volume persistente para armazenamento do banco de dados

### 5. Acessar a aplicação

Execute:

```powershell
minikube service frontend --url
```

Abra no navegador o endereço retornado pelo comando.

O frontend utiliza a porta 80 e o Service está configurado com NodePort `30080`.

## Arquitetura da aplicação

```text
             Navegador
                 |
                 v
       Service Frontend
        NodePort 30080
                 |
                 v
       Frontend Angular
          Nginx (1 Pod)
                 |
                 v
        Service Backend
        ClusterIP :8080
                 |
          +------+------+
          |             |
          v             v
      Backend 1     Backend 2
          |             |
          +------+------+
                 |
                 v
          Service MySQL
            Porta 3306
                 |
                 v
            MySQL 8.0
                 |
                 v
        PersistentVolumeClaim
```

O frontend utiliza o Nginx como servidor web e proxy reverso para o backend.

O backend se comunica com o MySQL por meio do Service interno do Kubernetes.

As configurações são gerenciadas utilizando ConfigMaps e Secrets.

## Disponibilidade e testes

### 1. Duas réplicas do backend

O Deployment do backend foi configurado com:

```yaml
replicas: 2
```

Essa configuração mantém duas instâncias do backend em execução.

Para verificar:

```powershell
kubectl get deployment backend
kubectl get pods -l app=backend -o wide
kubectl get endpointslices -l kubernetes.io/service-name=backend
```

Durante os testes, os dois Pods estavam em estado `Running` e registrados como endpoints do Service `backend`.

### 2. Recriação automática de Pods

Para demonstrar a recuperação automática, um dos Pods do backend foi excluído manualmente.

Primeiro, liste os Pods:

```powershell
kubectl get pods -l app=backend
```

Depois, exclua uma das réplicas:

```powershell
kubectl delete pod NOME_DO_POD
```

Acompanhe a recriação:

```powershell
kubectl get pods -w
```

O Kubernetes cria automaticamente um novo Pod para manter as duas réplicas configuradas no Deployment.

Durante o teste, foi possível observar o novo Pod sendo criado e entrando no estado `Running`.

Para encerrar o acompanhamento, pressione `Ctrl + C`.

### 3. Balanceamento de carga

Para demonstrar a distribuição de conexões por um Service Kubernetes, foi utilizado um Deployment auxiliar com duas réplicas da imagem `traefik/whoami`.

**Criar o Deployment de teste:**

```powershell
kubectl create deployment teste-lb --image=traefik/whoami --replicas=2 --port=80
```

**Criar o Service:**

```powershell
kubectl expose deployment teste-lb --port=80
```

**Verificar os recursos:**

```powershell
kubectl get pods -l app=teste-lb
kubectl get svc teste-lb
```

Para testar a distribuição das requisições, foi utilizado um Pod cliente temporário com BusyBox.

Execute no PowerShell:

```powershell
kubectl run cliente-lb --image=busybox:1.36 --restart=Never --rm -i -- sh -c 'for i in $(seq 1 20); do wget -qO- -T 5 http://teste-lb | grep Hostname; done'
```

O resultado apresentou diferentes nomes de Pods respondendo às requisições.

Isso demonstrou que o Service auxiliar encaminhou conexões para ambas as réplicas.

**Observação:** o teste demonstra o funcionamento do balanceamento no Service auxiliar. A distribuição efetiva das requisições do backend Spring Boot não foi medida diretamente.

**Remover os recursos auxiliares após o teste:**

```powershell
kubectl delete deployment teste-lb
kubectl delete service teste-lb
```

## Observações importantes

- As imagens Docker são construídas diretamente no ambiente do Minikube.
- Não é necessário publicar as imagens em um registry externo.
- O frontend utiliza Nginx, configurado no arquivo `siqueiraLanches/nginx.conf`.
- O backend utiliza ConfigMap para a URL do banco de dados.
- As credenciais do banco e a chave JWT são configuradas por meio de Secrets do Kubernetes.
- Os valores de credenciais presentes nos manifestos são destinados exclusivamente ao ambiente acadêmico de demonstração. Não devem ser reutilizados em produção.
- O MySQL utiliza um PersistentVolumeClaim para persistência dos dados.

## Conclusão

O projeto Siqueira Lanches foi implantado com sucesso em um ambiente Kubernetes local utilizando Minikube.

Durante a atividade, foram utilizados Dockerfiles, Deployments, Services, ConfigMaps, Secrets e armazenamento persistente.

Também foram realizados testes para demonstrar a comunicação entre os componentes, a utilização de múltiplas réplicas, a recriação automática de Pods e o balanceamento de carga em um Service auxiliar.

A atividade permitiu aplicar conceitos de conteinerização, orquestração e disponibilidade de aplicações.