package com.pelvictrainer.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class UserPreferencesRepositoryImpl_Factory implements Factory<UserPreferencesRepositoryImpl> {
  @Override
  public UserPreferencesRepositoryImpl get() {
    return newInstance();
  }

  public static UserPreferencesRepositoryImpl_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UserPreferencesRepositoryImpl newInstance() {
    return new UserPreferencesRepositoryImpl();
  }

  private static final class InstanceHolder {
    private static final UserPreferencesRepositoryImpl_Factory INSTANCE = new UserPreferencesRepositoryImpl_Factory();
  }
}
