import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Set;
import java.util.Random;

class Grafo {
    private int V; // Numero de vertices
    private ArrayList<ArrayList<Integer>> adj; // Lista de adjacencias

    Grafo(int v) {
        V = v;
        adj = new ArrayList<>(V);
        for (int i = 0; i < V; ++i)
            adj.add(new ArrayList<>());
    
        // Adiciona arestas aleatorias de forma mais esparsa
        Random rand = new Random();
        for (int i = 1; i < V; i++) {
            int vertice = rand.nextInt(i);
            adicionaAresta(i, vertice);
        }
        for (int i = 0; i < V; i++) {
            int numArestas = rand.nextInt(2); // Defina o numero maximo de arestas por vertice (por exemplo, 2)
            for (int j = 0; j < numArestas; j++) {
                int vertice = rand.nextInt(V);
                if (vertice != i && !adj.get(i).contains(vertice)) {
                    adicionaAresta(i, vertice);
                }
            }
        }
    }
    
    Grafo(int v, int w) {
        V = v;
        adj = new ArrayList<>(V);
        for (int i = 0; i < w; ++i)
            adj.add(new ArrayList<>());
    }

    // Funcao para adicionar uma aresta ao grafo
    void adicionaAresta(int v, int w) {
        adj.get(v).add(w);
        adj.get(w).add(v);
    }
    
    // Funcao para remover um vertice do grafo
    void removerVertice(int v) {
        adj.remove(v);
        for(int i = 0; i < adj.size(); i++){
            for(int j = 0; j < adj.get(i).size(); j++){
                if(adj.get(i).get(j) == v){
                    adj.get(i).remove(j);
                }
            }
        }
        V--;
        for(int i = 0; i < adj.size(); i++){
            for(int j = 0; j < adj.get(i).size(); j++){
                if(adj.get(i).get(j) > v){
                    int aux = adj.get(i).get(j);
                    adj.get(i).set(j, aux-1);
                }
            }
        }
    }
    
    // Funcao para criar uma copia do Grafo
    public Grafo copiarGrafo() {
        Grafo novoGrafo = new Grafo(V, V);
        for(int i = 0; i < V; i++){
            for(int j = 0; j < adj.get(i).size(); j++){
                int u = adj.get(i).get(j);
                if(u >= i){
                    novoGrafo.adicionaAresta(i, u);
                }
            }
        }
        return novoGrafo;
    }

    // Funcao de utilidade para verificar se existe um ciclo
    // a partir do vertice v
    private void isCicloUtil(int v, boolean[] visitados, int pai, ArrayList<Integer> cicloAtual, HashSet<HashSet<Integer>> ciclos) {
        // Marca o vertice atual como visitado
        visitados[v] = true;
        cicloAtual.add(v);

        // Recurra para todos os vertices adjacentes ao vertice atual
        for (Integer i : adj.get(v)) {
            // Se o vertice adjacente nao foi visitado, entao o visite
            if (!visitados[i]) {
                isCicloUtil(i, visitados, v, cicloAtual, ciclos);
            }
            // Se o vertice adjacente e visitado e nao e o pai do vertice atual,
            // entao ha um ciclo no grafo
            else if (i != pai) {
                // Encontrou um ciclo, adiciona ao HashSet de ciclos
                ArrayList<Integer> ciclo = new ArrayList<>(cicloAtual.subList(cicloAtual.indexOf(i), cicloAtual.size()));
                ciclo.add(v);
                ciclos.add(new HashSet<>(ciclo));
            }
        }

        cicloAtual.remove(cicloAtual.size() - 1);
        visitados[v] = false;
    }

    // Funcao principal para verificar se o grafo contem um ciclo
    ArrayList<ArrayList<Integer>> encontraCiclos() {
        // Marca todos os vertices como nao visitados
        boolean[] visitados = new boolean[V];
        Arrays.fill(visitados, false);

        // HashSet para armazenar todos os ciclos encontrados
        HashSet<HashSet<Integer>> ciclosSet = new HashSet<>();

        // Lista temporaria para armazenar o ciclo atual sendo explorado
        ArrayList<Integer> cicloAtual = new ArrayList<>();

        // Chama a funcao de utilidade recursiva para encontrar todos os ciclos
        for (int u = 0; u < V; u++)
            if (!visitados[u])
                isCicloUtil(u, visitados, -1, cicloAtual, ciclosSet);

        // Converter HashSet para TreeSet com comparador personalizado para garantir ordenacao pela soma
        TreeSet<ArrayList<Integer>> ciclosOrdenados = new TreeSet<>(new Comparator<ArrayList<Integer>>() {
            @Override
            public int compare(ArrayList<Integer> ciclo1, ArrayList<Integer> ciclo2) {
                int sum1 = somaDoCiclo(ciclo1);
                int sum2 = somaDoCiclo(ciclo2);
                return Integer.compare(sum1, sum2);
            }
        });
        for (HashSet<Integer> cicloSet : ciclosSet) {
            ArrayList<Integer> ciclo = new ArrayList<>(cicloSet);
            ciclosOrdenados.add(ciclo);
        }

        // Converter TreeSet para ArrayList
        ArrayList<ArrayList<Integer>> ciclos = new ArrayList<>(ciclosOrdenados);

        return ciclos;
    }

    // Funcao para encontrar a soma dos vertices em um ciclo
    private int somaDoCiclo(ArrayList<Integer> ciclo) {
        int sum = 0;
        for (int vertice : ciclo) {
            sum += vertice;
        }
        return sum;
    }
    
    //Funcado dfs sem alteracoes
    private void dfsOriginal(int vertice, Set<Integer> visitados) {
        visitados.add(vertice);
        for (int vizinho : adj.get(vertice)) {
            if (!visitados.contains(vizinho)) {
                dfsOriginal(vizinho, visitados);
            }
        }
    }
    
    // Funcao para encontrar a quantidade de componentes conexos do grafo
    public int componentesConexos(){
        int componentes = 0;
        Set<Integer> visitados = new HashSet<>();

        for (int vertice = 0; vertice < V; vertice++) {
            if (!visitados.contains(vertice)) {
                dfsOriginal(vertice, visitados);
                componentes++;
            }
        }
        return componentes;
    } 
    
    // Funcao para encontrar articulacoes a partir dos componentes conexos
    public ArrayList<Integer> encontrarArticulacoes(){
        ArrayList<Integer> articulacoes = new ArrayList<>();
        for(int i = 0; i < V; i++){
            Grafo aux = this.copiarGrafo();
            aux.removerVertice(i);
            if(aux.componentesConexos() > this.componentesConexos()){
                articulacoes.add(i);
            }
        }
        
        return articulacoes;
    }
    
    // Funcao que encontra articulacoes
    void findArticulationsTarjan() {
        boolean[] visited = new boolean[V];
        int[] disc = new int[V]; // Tempo de descoberta
        int[] low = new int[V]; // Menor tempo de descoberta alcancavel
        int[] parent = new int[V]; // Vertice pai na arvore DFS
        int time = 0;

        Arrays.fill(parent, -1);

        ArrayList<Integer> articulacoes = new ArrayList<>(); // Lista para armazenar as articulacoes

        for (int i = 0; i < V; i++) {
            if (!visited[i]) {
                dfs(i, visited, disc, low, parent, time, articulacoes); // Chamada recursiva do DFS
            }
        }

        // Impressao das articulacoes encontradas
        System.out.println("Articulacoes encontradas:");
        for (int articulacao : articulacoes) {
            System.out.println(articulacao);
        }

        // Verificar e imprimir blocos de articulacoes adjacentes
        verificarBlocosDeArticulacoes(articulacoes);
    }

    // Metodo para verificar e imprimir blocos de articulacoes adjacentes
    void verificarBlocosDeArticulacoes(ArrayList<Integer> articulacoes) {
        System.out.println("Blocos formados por apenas 2 vertices:");
        for (int i = 0; i < articulacoes.size() - 1; i++) {
            int art1 = articulacoes.get(i);
            for (int j = i + 1; j < articulacoes.size(); j++) {
                int art2 = articulacoes.get(j);
                // Verifica se as articulacoes sao adjacentes
                if (adj.get(art1).contains(art2)) {
                    System.out.println( "[" + art1 + " , " + art2 + "]");
                }
            }
        }
    }

    void dfs(int u, boolean[] visited, int[] disc, int[] low, int[] parent, int time, ArrayList<Integer> articulacoes) {
        visited[u] = true;
        disc[u] = low[u] = ++time;

        int children = 0; // Contador de filhos na arvore DFS

        for (int v : adj.get(u)) {
            if (!visited[v]) {
                children++;
                parent[v] = u;
                dfs(v, visited, disc, low, parent, time, articulacoes);

                low[u] = Math.min(low[u], low[v]);

                // Verifica se u e uma articulacao
                if ((parent[u] == -1 && children > 1) || (parent[u] != -1 && low[v] >= disc[u])) {
                    articulacoes.add(u); // Adiciona a articulacao encontrada a lista
                }
            } else if (v != parent[u]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }
    
    //Funcao para encontrar blocos a partir dos ciclos
    ArrayList<ArrayList<Integer>> blocos(){
        ArrayList<ArrayList<Integer>> blocos = this.encontraCiclos(); //blocos inicialmente sao iguais aos ciclos
        
        //compara um ciclo ao proximo ciclo em loop unificando-os em blocos caso tenham dois ou mais vertices em comum ate que nao seja mais possivel
        for (int i = 0; i < blocos.size(); i++){
            ArrayList<Integer> bloco = blocos.get(i);
          for (int j = i+1; j < blocos.size(); j++){
              ArrayList<Integer> blocoAux = blocos.get(j);
            Set<Integer> intersecao = new HashSet<>(blocos.get(i));
            intersecao.retainAll(blocos.get(j)); //encontra a intersecao entre os ciclos
            if(intersecao.size() >= 2){
                Set<Integer> uniao = new HashSet<>(blocos.get(i));
                uniao.addAll(blocos.get(j));
                blocos.remove(bloco);
                blocos.remove(blocoAux);
                blocos.add(new ArrayList<>(uniao)); //remove do conjunto de blocos os ciclos sozinhos e adiciona a sua uniao caso tenham mais de dois vertices em comum
            }
         }
       }
       
       return blocos;
    }
    
    // Metodo para encontrar e imprimir blocos de articulacoes adjacentes
    void encontraBlocosDeArticulacoes() {
        ArrayList<Integer> articulacoes = new ArrayList<>();
        
        for (int i = 0; i < V; i++) {
            if (adj.get(i).size() >= 2) {
                articulacoes.add(i);
            }
        }
        
        if (articulacoes.size() == 2) {
            int art1 = articulacoes.get(0);
            int art2 = articulacoes.get(1);
            
            // Verifica se as duas articulacos sao adjacentes
            if (adj.get(art1).contains(art2)) {
                System.out.println("Articulacao 1: " + art1);
            }
        }
    }

    // Metodo de teste
    public static void main(String args[]) {
        Grafo g = new Grafo(10);

        // Encontra e imprime os ciclos e blocos
        ArrayList<ArrayList<Integer>> ciclos = g.encontraCiclos();
        ArrayList<ArrayList<Integer>> blocos = g.blocos();
        if (ciclos.isEmpty()) {
            System.out.println("Ex 1 : Encontrando blocos atraves dos ciclos");
            System.out.println("O grafo nao contem ciclos.");
        } else {
            System.out.println("Ex 1 : Encontrando blocos atraves dos ciclos");
            System.out.println("Ciclos encontrados:");
            for (ArrayList<Integer> ciclo : ciclos) {
                System.out.println(ciclo);
            }
            System.out.println("Blocos encontrados:");
            for (ArrayList<Integer> bloco : blocos) {
                System.out.println(bloco);
            }
        }

        System.out.println("\nEx 2: Testando Conectividade com remocao e insercao de vertices");
        // Encontra articulacoes usando o metodo de verificar os componentes conexos
        System.out.println("Articulacoes: "+ g.encontrarArticulacoes());

        System.out.println("\nEx 3: Tarjan");

        System.out.println("Blocos encontrados:");
        for (ArrayList<Integer> bloco : blocos) {
            System.out.println(bloco);
        }

        // Encontra e imprime os blocos de articulacoes adjacentes
        g.encontraBlocosDeArticulacoes();

        // Encontra e imprime os pontos de articulacao usando Tarjan
        g.findArticulationsTarjan();
        
       
    }
}