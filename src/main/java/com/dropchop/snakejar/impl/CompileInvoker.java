package com.dropchop.snakejar.impl;

import com.dropchop.snakejar.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author Nikola Ivačič <nikola.ivacic@dropchop.org> on 5. 11. 21.
 */
public class CompileInvoker extends Invoker {

  private static final Logger LOG = LoggerFactory.getLogger(CompileInvoker.class);

  private final List<Source<?>> sources;

  public CompileInvoker(InterpreterFactory interpreterFactory, ExecutorService service, List<Source<?>> sources) {
    super(interpreterFactory, service);
    this.sources = sources;
  }

  protected List<Source<?>> getSources() {
    return sources;
  }

  @Override
  public <X, V> Future<V> apply(Invocation<V> invocation, Arg<X> argSuplier, KwArg kwSuplier) {
    List<Source<?>> sources = this.getSources();
    return this.getService().submit(
      () -> {
        InterpreterFactory interpreterFactory = getInterpreterFactory();
        LOG.trace("Get interpreter from [{}]...", interpreterFactory);
        com.dropchop.snakejar.Interpreter interpreter = interpreterFactory.getInterpreter();
        LOG.trace("Got interpreter from [{}].", interpreterFactory);
        try {
          interpreter.compile(sources);
        } catch (Exception e) {
          throw new Exception("Unable to compile source! Will not execute", e);
        }
        return null;
      }
    );
  }
}
