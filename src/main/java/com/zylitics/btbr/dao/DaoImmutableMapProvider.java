package com.zylitics.btbr.dao;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.zylitics.btbr.runner.provider.ImmutableMapProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DaoImmutableMapProvider extends AbstractDaoProvider implements ImmutableMapProvider {
  
  @Autowired
  DaoImmutableMapProvider(NamedParameterJdbcTemplate jdbc) {
    super(jdbc);
  }
  
  @Override
  public Optional<Map<String, String>> getMapFromTable(String table) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(table), "table is required");
    
    String sql = "SELECT key, value FROM " + table;
    
    List<Map.Entry<String, String>> l = jdbc.query(sql, (rs, rowNum) ->
        new AbstractMap.SimpleImmutableEntry<>(rs.getString("key"), rs.getString("value")));
    
    if (l.size() == 0) {
      return Optional.empty();
    }
    //noinspection UnstableApiUsage
    return Optional.of(ImmutableMap.<String, String>builder().putAll(l).build());
  }
}