package com.pelvictrainer.data.repository;

import com.pelvictrainer.database.dao.TrainingDao;
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
public final class TrainingRepositoryImpl_Factory implements Factory<TrainingRepositoryImpl> {
  private final Provider<TrainingDao> daoProvider;

  public TrainingRepositoryImpl_Factory(Provider<TrainingDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public TrainingRepositoryImpl get() {
    return newInstance(daoProvider.get());
  }

  public static TrainingRepositoryImpl_Factory create(Provider<TrainingDao> daoProvider) {
    return new TrainingRepositoryImpl_Factory(daoProvider);
  }

  public static TrainingRepositoryImpl newInstance(TrainingDao dao) {
    return new TrainingRepositoryImpl(dao);
  }
}
