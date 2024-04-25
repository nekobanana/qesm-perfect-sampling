package org.example.model.generator;

import org.la4j.Matrix;

import java.util.*;

public class Tarjan {

    private Matrix incidenceMatrix;
    private int N;
    private List<List<Integer>> components;
    private int[] ids, low;
    private boolean[] onStack;
    private Deque<Integer> stack;
    private int id, sccCount;

    public Tarjan(Matrix matrix) {
        incidenceMatrix = matrix;
        N = matrix.rows();
        ids = new int[N];
        low = new int[N];
        onStack = new boolean[N];
        stack = new ArrayDeque<>();
        components = new ArrayList<>();
        Arrays.fill(ids, -1);

        for (int i = 0; i < N; i++) {
            if (ids[i] == -1) {
                dfs(i);
            }
        }
    }

    private void dfs(int at) {
        stack.push(at);
        onStack[at] = true;
        ids[at] = low[at] = id++;

        for (int i = 0; i < N; i++) {
            if (incidenceMatrix.get(at, i) != 0) {
                int to = i;
                if (ids[to] == -1) {
                    dfs(to);
                }
                if (onStack[to]) {
                    low[at] = Math.min(low[at], low[to]);
                }
            }
        }

        if (ids[at] == low[at]) {
            List<Integer> component = new ArrayList<>();
            for (int node = stack.pop(); ; node = stack.pop()) {
                onStack[node] = false;
                low[node] = ids[at];
                component.add(node);
                if (node == at) break;
            }
            components.add(component);
            sccCount++;
        }
    }

    public List<List<Integer>> getComponents() {
        return components;
    }

}