package interpreter;

import interpreter.exceptions.SymbolNotFoundException;
import interpreter.types.MSFunction;
import interpreter.types.MSInbuiltFunction;
import interpreter.types.MSType;

import java.util.*;

public class SymbolTable {
    private final Map<String, Symbol> hashMap = new HashMap<>();
    private final Stack<ArrayList<String>> scopeStack = new Stack<>();

    public SymbolTable() {
        scopeStack.push(new ArrayList<>());
    }

    public void enterScope() {
        scopeStack.push(new ArrayList<>());
    }

    public void exitScope() {
        for (String symbolName : scopeStack.peek()) {
            delete(symbolName);
        }
        scopeStack.pop();
    }

    public void enterSymbol(String name, MSType value) {
        Symbol newSymbol = new Symbol(name, value);
        checkRestrictedKeyWords(newSymbol);

        if (isVarInNewScope(name)) {
            Symbol oldSymbol = hashMap.get(getPrefixName(name));
            delete(oldSymbol.name);
            Symbol prefixSymbol = new Symbol(oldSymbol.name, value);
            add(prefixSymbol);
            return;
        }

        if (hashMap.containsKey(name)) {
            delete(newSymbol.name);
        } else {
            scopeStack.peek().add(newSymbol.name);
        }
        add(newSymbol);
    }

    private void checkRestrictedKeyWords(Symbol symbol) {
        for (MSInbuiltFunction funcName : MSInbuiltFunction.values()) {
            if (symbol.value instanceof MSFunction f) {
                if (symbol.name.equals(funcName.name())) {
                    throw new RuntimeException("Cannot declare function with restricted name: " + funcName.name());
                }
                else if (f.getParameters().stream().anyMatch(p -> p.equals(funcName.name()))) {
                    throw new RuntimeException("Cannot declare function with restricted parameter name: " + funcName.name());
                }
            }
            else if (symbol.name.equals(funcName.name())){
                throw new RuntimeException("Cannot declare variable with restricted name: " + funcName.name());
            }
        }
    }

    public Symbol retrieveSymbol(String name) {
        if (isVarInNewScope(name)) {
            return hashMap.get(getPrefixName(name));
        } else if (hashMap.containsKey(name)) {
            return hashMap.get(name);
        } else {
            throw new SymbolNotFoundException("Could not find symbol in symbol table: " + name);
        }
    }

    public MSType retrieveSymbolValue(Symbol symbol) {
        return symbol.value;
    }

    private void delete(String name) {
        hashMap.remove(name);
    }

    private void add(Symbol symbol) {
        hashMap.put(symbol.name, symbol);
    }

    /**
     * @param name name of the variable
     * @return true if the variable is in the current scope
     */
    private boolean isVarInNewScope(String name) {
        return scopeStack.peek().stream().anyMatch(s -> s.endsWith("." + name));
    }

    private String getPrefixName(String name) {
        return scopeStack.peek().stream().filter(s -> s.contains("." + name)).findFirst().orElseThrow();
    }

    private record Symbol(String name, MSType value) {
    }
}

