package cl.dsoto.trading.components;

import cl.dsoto.trading.model.Strategy;

import java.util.List;

/**
 * Created by des01c7 on 25-03-19.
 */
public interface StrategyManager {

    public Strategy findBy(String name, int variables);

    public List<Strategy> getAll() throws Exception;

    public List<Strategy> getIntegerProblemTypeStrategies() throws Exception;

    public List<Strategy> getBinaryProblemTypeStrategies() throws Exception;

    public Strategy getByName(String name);

}
