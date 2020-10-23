package interfaces;

@FunctionalInterface
public interface Task<T> {
    void exec(T t);
}
