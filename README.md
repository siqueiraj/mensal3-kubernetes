Siqueira Lanches — Mensal 3: Kubernetes
Projeto acadêmico de implantação da aplicação Siqueira Lanches no Kubernetes local utilizando Minikube.

Integrantes: João Victor Siqueira, Lucas Eduardo Schneider e Carlos Feliponi.
Tecnologias
- Frontend: Angular, servido pelo Nginx
- Backend: Java 17, Spring Boot e Maven
- Banco de dados: MySQL
- Contêineres: Docker
- Orquestração: Kubernetes com Minikube
Estrutura do repositório
kubernet/
├── k8s/
│   ├── backend.yaml
│   ├── frontend.yaml
│   └── mysql.yaml
├── siqueira-lanches-backend/
│   └── Dockerfile
├── siqueiraLanches/
│   └── Dockerfile
└── README.md
As pastas de frontend e backend contêm também seus respectivos códigos-fonte.
Pré-requisitos
- Docker Desktop iniciado e configurado para contêineres Linux
- Minikube
- kubectl
- Git (para clonar o repositório)
Como executar no Minikube
Abra um terminal na raiz do repositório.
1. Iniciar o cluster
minikube start --driver=docker
minikube status
2. Construir as imagens no ambiente do Minikube
Como os Deployments utilizam imagePullPolicy: Never, as imagens precisam estar disponíveis localmente no Minikube.
minikube image build -t siqueira-backend:1.0 .\siqueira-lanches-backend
minikube image build -t siqueira-frontend:1.2 .\siqueiraLanches
3. Aplicar os manifestos Kubernetes
kubectl apply -f .\k8s\mysql.yaml
kubectl rollout status deployment/mysql --timeout=180s
kubectl apply -f .\k8s\backend.yaml
kubectl rollout status deployment/backend --timeout=180s
kubectl apply -f .\k8s\frontend.yaml
kubectl rollout status deployment/frontend --timeout=180s
Os comandos pressupõem que o Deployment do banco se chama mysql, como no ambiente utilizado no trabalho. Confirme esse nome no arquivo k8s/mysql.yaml.

4. Conferir os recursos
kubectl get pods -o wide
kubectl get services
kubectl get deployments
kubectl get pvc
5. Abrir a aplicação
minikube service frontend --url
Abra no navegador o endereço exibido pelo comando. O Service frontend usa a porta 80 e o NodePort 30080.
Arquitetura
Navegador
   |
   v
Service frontend (NodePort 30080)
   |
   v
Frontend Angular + Nginx (1 réplica)
   |
   v
Service backend (ClusterIP, porta 8080)
   |                         |
   v                         v
Backend Pod 1             Backend Pod 2
   |                         |
   +------------+------------+
                |
                v
         Service MySQL
                |
                v
          MySQL + PVC
O backend utiliza ConfigMap para a URL do banco e Secret para credenciais e chave JWT. Para demonstração local, revise os valores antes de publicar o repositório. Não publique credenciais reais.
Disponibilidade e testes
Duas réplicas do backend
O Deployment backend foi configurado com replicas: 2. Os dois Pods foram observados em estado Running e registrados como endpoints do Service backend.
kubectl get deployment backend
kubectl get pods -l app=backend -o wide
kubectl get endpointslices -l kubernetes.io/service-name=backend
Recriação automática de Pod
Foi excluído manualmente um dos Pods do backend. O Kubernetes criou outro Pod para restabelecer as duas réplicas.

Para reproduzir, primeiro liste os Pods e escolha um nome atual:
kubectl get pods -l app=backend
kubectl delete pod NOME_DO_POD
kubectl get pods -w
Interrompa o acompanhamento com Ctrl+C depois de observar os dois Pods Running.
Balanceamento de carga — teste auxiliar
Para demonstrar a distribuição de conexões por um Service Kubernetes, foi criado um Deployment temporário com duas réplicas da imagem traefik/whoami, exposto pelo Service teste-lb.
kubectl create deployment teste-lb --image=traefik/whoami --replicas=2 --port=80
kubectl expose deployment teste-lb --port=80
kubectl get pods -l app=teste-lb
kubectl get svc teste-lb
A partir de um Pod cliente, foram feitas requisições ao Service teste-lb. A saída mostrou hostnames diferentes, comprovando que ambas as réplicas atenderam conexões. Esse teste demonstra o balanceamento do Service auxiliar, não mede diretamente a distribuição de requisições do backend Spring Boot.

Após o teste, os recursos auxiliares podem ser removidos:
kubectl delete deployment teste-lb
kubectl delete service teste-lb
Observações
- As imagens são construídas localmente no Minikube e não precisam estar publicadas em um registry para este procedimento.
- O frontend é servido pelo Nginx; sua configuração de proxy está em siqueiraLanches/nginx.conf.
- A configuração de banco e credenciais para o backend está nos recursos ConfigMap e Secret de k8s/backend.yaml.
- Não versionar senhas reais, tokens ou chaves privadas.