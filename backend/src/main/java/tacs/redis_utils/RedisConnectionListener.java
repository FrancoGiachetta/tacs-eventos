package tacs.redis_utils;

import lombok.AllArgsConstructor;
import org.redisson.api.NodeType;
import org.redisson.connection.ConnectionListener;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.List;

@Service
@AllArgsConstructor
public class RedisConnectionListener implements ConnectionListener {
    private final List<InicializadorRedis> inicializadores;

    @Override
    public void onConnect(InetSocketAddress inetSocketAddress) {
        inicializarTodos();
    }

    @Override
    public void onConnect(InetSocketAddress addr, NodeType nodeType) {
        inicializarTodos();
    }

    @Override
    public void onDisconnect(InetSocketAddress inetSocketAddress) {
    }

    @Override
    public void onDisconnect(InetSocketAddress addr, NodeType nodeType) {
    }

    private void inicializarTodos() {
        inicializadores.forEach(InicializadorRedis::inicializar);
    }
}
