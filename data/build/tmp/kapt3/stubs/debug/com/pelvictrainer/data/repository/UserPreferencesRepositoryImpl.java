package com.pelvictrainer.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0096@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\u000b2\u0006\u0010\u0010\u001a\u00020\u0011H\u0096@\u00a2\u0006\u0002\u0010\u0012R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00078VX\u0096\u0004\u00a2\u0006\u0006\u001a\u0004\b\b\u0010\t\u00a8\u0006\u0013"}, d2 = {"Lcom/pelvictrainer/data/repository/UserPreferencesRepositoryImpl;", "Lcom/pelvictrainer/domain/repository/UserPreferencesRepository;", "()V", "_userPreferences", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/pelvictrainer/domain/model/UserPreferences;", "userPreferences", "Lkotlinx/coroutines/flow/Flow;", "getUserPreferences", "()Lkotlinx/coroutines/flow/Flow;", "setNotificationsEnabled", "", "enabled", "", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setTrainingLevel", "level", "Lcom/pelvictrainer/domain/model/TrainingLevel;", "(Lcom/pelvictrainer/domain/model/TrainingLevel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "data_debug"})
public final class UserPreferencesRepositoryImpl implements com.pelvictrainer.domain.repository.UserPreferencesRepository {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<com.pelvictrainer.domain.model.UserPreferences> _userPreferences = null;
    
    @javax.inject.Inject()
    public UserPreferencesRepositoryImpl() {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public kotlinx.coroutines.flow.Flow<com.pelvictrainer.domain.model.UserPreferences> getUserPreferences() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object setTrainingLevel(@org.jetbrains.annotations.NotNull()
    com.pelvictrainer.domain.model.TrainingLevel level, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public java.lang.Object setNotificationsEnabled(boolean enabled, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}