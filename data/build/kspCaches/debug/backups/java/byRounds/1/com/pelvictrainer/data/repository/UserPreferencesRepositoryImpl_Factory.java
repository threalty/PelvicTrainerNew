package com.pelvictrainer.data.repository;

import com.pelvictrainer.datastore.PelvicDataStore;
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
  private final Provider<PelvicDataStore> dataStoreProvider;

  public UserPreferencesRepositoryImpl_Factory(Provider<PelvicDataStore> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public UserPreferencesRepositoryImpl get() {
    return newInstance(dataStoreProvider.get());
  }

  public static UserPreferencesRepositoryImpl_Factory create(
      Provider<PelvicDataStore> dataStoreProvider) {
    return new UserPreferencesRepositoryImpl_Factory(dataStoreProvider);
  }

  public static UserPreferencesRepositoryImpl newInstance(PelvicDataStore dataStore) {
    return new UserPreferencesRepositoryImpl(dataStore);
  }
}
