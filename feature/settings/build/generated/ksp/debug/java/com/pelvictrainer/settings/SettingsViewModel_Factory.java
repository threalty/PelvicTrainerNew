package com.pelvictrainer.settings;

import com.pelvictrainer.domain.analytics.AnalyticsTracker;
import com.pelvictrainer.domain.auth.AuthRepository;
import com.pelvictrainer.domain.repository.UserPreferencesRepository;
import com.pelvictrainer.domain.subscription.SubscriptionRepository;
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

  private final Provider<SubscriptionRepository> subscriptionRepositoryProvider;

  public SettingsViewModel_Factory(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      Provider<AnalyticsTracker> analyticsProvider, Provider<AuthRepository> authRepositoryProvider,
      Provider<SubscriptionRepository> subscriptionRepositoryProvider) {
    this.userPreferencesRepositoryProvider = userPreferencesRepositoryProvider;
    this.analyticsProvider = analyticsProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.subscriptionRepositoryProvider = subscriptionRepositoryProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(userPreferencesRepositoryProvider.get(), analyticsProvider.get(), authRepositoryProvider.get(), subscriptionRepositoryProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider,
      Provider<AnalyticsTracker> analyticsProvider, Provider<AuthRepository> authRepositoryProvider,
      Provider<SubscriptionRepository> subscriptionRepositoryProvider) {
    return new SettingsViewModel_Factory(userPreferencesRepositoryProvider, analyticsProvider, authRepositoryProvider, subscriptionRepositoryProvider);
  }

  public static SettingsViewModel newInstance(UserPreferencesRepository userPreferencesRepository,
      AnalyticsTracker analytics, AuthRepository authRepository,
      SubscriptionRepository subscriptionRepository) {
    return new SettingsViewModel(userPreferencesRepository, analytics, authRepository, subscriptionRepository);
  }
}
