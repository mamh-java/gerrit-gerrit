// Copyright (C) 2017 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gerrit.server.query.group;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.flogger.FluentLogger;
import com.google.gerrit.entities.Account;
import com.google.gerrit.entities.AccountGroup;
import com.google.gerrit.entities.InternalGroup;
import com.google.gerrit.index.IndexConfig;
import com.google.gerrit.index.query.InternalQuery;
import com.google.gerrit.index.query.Predicate;
import com.google.gerrit.server.index.group.GroupIndexCollection;
import com.google.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Query wrapper for the group index.
 *
 * <p>Instances are one-time-use. Other singleton classes should inject a Provider rather than
 * holding on to a single instance.
 */
public class InternalGroupQuery extends InternalQuery<InternalGroup, InternalGroupQuery> {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  @Inject
  InternalGroupQuery(
      GroupQueryProcessor queryProcessor, GroupIndexCollection indexes, IndexConfig indexConfig) {
    super(queryProcessor, indexes, indexConfig);
  }

  public Optional<InternalGroup> byName(AccountGroup.NameKey groupName) {
    return getOnlyGroup(GroupPredicates.name(groupName.get()), "group name '" + groupName + "'");
  }

  public Optional<InternalGroup> byId(AccountGroup.Id groupId) {
    return getOnlyGroup(GroupPredicates.id(groupId), "group id '" + groupId + "'");
  }

  public List<InternalGroup> byMember(Account.Id memberId) {
    return query(GroupPredicates.member(memberId));
  }

  /**
   * Get all immediate parents of the provided {@code subgroupIds}.
   *
   * @return map pointing from children to list of its immediate parents
   */
  public Map<AccountGroup.UUID, ImmutableSet<AccountGroup.UUID>> bySubgroups(
      ImmutableSet<AccountGroup.UUID> subgroupIds) {
    List<Predicate<InternalGroup>> predicates =
        subgroupIds.stream().map(e -> GroupPredicates.subgroup(e)).collect(Collectors.toList());
    List<InternalGroup> groups = query(Predicate.or(predicates));

    Map<AccountGroup.UUID, Set<AccountGroup.UUID>> parentsByChild =
        Maps.newHashMapWithExpectedSize(groups.size());
    subgroupIds.stream().forEach(c -> parentsByChild.put(c, new HashSet<>()));
    for (InternalGroup parent : groups) {
      for (AccountGroup.UUID child : parent.getSubgroups()) {
        if (subgroupIds.contains(child)) {
          parentsByChild.get(child).add(parent.getGroupUUID());
        }
      }
    }
    return parentsByChild.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> ImmutableSet.copyOf(e.getValue())));
  }

  private Optional<InternalGroup> getOnlyGroup(
      Predicate<InternalGroup> predicate, String groupDescription) {
    List<InternalGroup> groups = query(predicate);
    if (groups.isEmpty()) {
      return Optional.empty();
    }

    if (groups.size() == 1) {
      return Optional.of(Iterables.getOnlyElement(groups));
    }

    ImmutableList<AccountGroup.UUID> groupUuids =
        groups.stream().map(InternalGroup::getGroupUUID).collect(toImmutableList());
    logger.atWarning().log("Ambiguous %s for groups %s.", groupDescription, groupUuids);
    return Optional.empty();
  }
}
