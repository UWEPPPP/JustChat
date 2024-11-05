package www.raven.jc.ws;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ws message handler factory
 *
 * @author Rawven
 * @date 2024/09/02
 */
@Component
public class WsMessageHandlerFactory {

	private Map<String, WsMessageHandler> handlerMap;

	@Autowired
	public WsMessageHandlerFactory(List<WsMessageHandler> handlerList) {
		handlerMap = handlerList.stream()
				.collect(Collectors.toMap(WsMessageHandler::getType, Function.identity()));
	}

	public WsMessageHandler getHandler(String type) {
		return handlerMap.get(type);
	}
}
