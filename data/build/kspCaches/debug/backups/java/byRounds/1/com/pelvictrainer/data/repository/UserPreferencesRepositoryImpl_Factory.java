package com.pelvictrainer.data.repository;

import com.pelvictrainer.datastore.PreferencesDataStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
  private final Provider<PreferencesDataStore> dataStoreProvider;

  public UserPreferencesRepositoryImpl_Factory(Provider<PreferencesDataStore> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public UserPreferencesRepositoryImpl get() {
    return newInstance(dataStoreProvider.get());
  }

  public static UserPreferencesRepositoryImpl_Factory create(
      Provider<PreferencesDataStore> dataStoreProvider) {
    return new UserPreferencesRepositoryImpl_Factory(dataStoreProvider);
  }

  public static UserPreferencesRepositoryImpl newInstance(PreferencesDataStore dataStore) {
    return new UserPreferencesRepositoryImpl(dataStore);
  }
}
