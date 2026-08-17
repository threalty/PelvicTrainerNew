package com.pelvictrainer.settings;

import com.pelvictrainer.domain.analytics.AnalyticsTracker;
import com.pelvictrainer.domain.auth.AuthRepository;
import com.pelvictrainer.domain.repository.UserPreferencesRepository;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<UserPreferencesRepository> userPreferencesRepositoryProvider;

  private final Provider<AnalyticsTracker> analyticsProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  public SettingsViewModel_Factory(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      Provider<AnalyticsTracker> analyticsProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    this.userPreferencesRepositoryProvider = userPreferencesRepositoryProvider;
    this.analyticsProvider = analyticsProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(userPreferencesRepositoryProvider.get(), analyticsProvider.get(), authRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      Provider<AnalyticsTracker> analyticsProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    return new SettingsViewModel_Factory(userPreferencesRepositoryProvider, analyticsProvider, authRepositoryProvider);
  }

  public static SettingsViewModel newInstance(UserPreferencesRepository userPreferencesRepository,
      AnalyticsTracker analytics, AuthRepository authRepository) {
    return new SettingsViewModel(userPreferencesRepository, analytics, authRepository);
  }
}
