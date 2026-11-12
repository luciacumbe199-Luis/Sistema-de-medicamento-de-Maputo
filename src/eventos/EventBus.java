package eventos;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/** Barramento síncrono: encaminha cada evento aos consumidores registados.. */
public final class EventBus {
    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<Consumer<?>>> listeners = new ConcurrentHashMap<>();

    public <T> void subscribe(Class<T> tipo, Consumer<T> listener) {
        listeners.computeIfAbsent(tipo, chave -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> void publish(T evento) {
        if (evento == null) throw new IllegalArgumentException("O evento não pode ser null.");
        List<Consumer<?>> subscritores = listeners.getOrDefault(evento.getClass(), new CopyOnWriteArrayList<>());
        for (Consumer<?> subscritor : subscritores) {
            ((Consumer<T>) subscritor).accept(evento);
        }
    }
}