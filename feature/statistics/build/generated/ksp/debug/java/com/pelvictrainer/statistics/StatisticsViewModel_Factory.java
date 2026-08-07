package com.pelvictrainer.statistics;

import com.pelvictrainer.domain.repository.TrainingRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class StatisticsViewModel_Factory implements Factory<StatisticsViewModel> {
  private final Provider<TrainingRepository> trainingRepositoryProvider;

  public StatisticsViewModel_Factory(Provider<TrainingRepository> trainingRepositoryProvider) {
    this.trainingRepositoryProvider = trainingRepositoryProvider;
  }

  @Override
  public StatisticsViewModel get() {
    return newInstance(trainingRepositoryProvider.get());
  }

  public static StatisticsViewModel_Factory create(
      Provider<TrainingRepository> trainingRepositoryProvider) {
    return new StatisticsViewModel_Factory(trainingRepositoryProvider);
  }

  public static StatisticsViewModel newInstance(TrainingRepository trainingRepository) {
    return new StatisticsViewModel(trainingRepository);
  }
}
