# GreenRoute 🌱

Aplicação Android para planeamento de rotas ecológicas com cálculo de emissões de CO₂.

## 📱 Funcionalidades

- **Home**: Mapa interativo, viagens recentes, pesquisa rápida
- **Viagens Salvas**: Guardar e gerir rotas favoritas
- **Histórico**: Ver todas as viagens realizadas
- **Pesquisa**: Comparar opções de transporte por emissão CO₂

## 🛠️ Tecnologias

- **Kotlin 1.9+**
- **Jetpack Compose** - UI moderna declarativa
- **Room Database** - Persistência local SQLite
- **Navigation Component** - Navegação entre ecrãs
- **Coroutines & Flow** - Programação assíncrona
- **Material 3** - Design system

## 📋 Requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK 34
- Dispositivo/Emulador API 24+ (Android 7.0)

## 🚀 Como Executar

1. **Clonar o projeto**
   ```bash
   git clone <repo-url>
   cd GreenRoute
   ```

2. **Abrir no Android Studio**
   - File → Open → Selecionar pasta GreenRoute

3. **Sincronizar Gradle**
   - Android Studio irá sincronizar automaticamente
   - Ou: File → Sync Project with Gradle Files

4. **Executar**
   - Selecionar dispositivo/emulador
   - Clicar Run (▶️) ou `Shift+F10`

## 🏗️ Estrutura do Projeto

```
app/src/main/java/com/greenroute/app/
├── data/
│   ├── local/
│   │   ├── entities/     # Room entities (9 tabelas)
│   │   ├── dao/          # Data Access Objects
│   │   └── database/     # AppDatabase
│   └── repository/       # Repositories
├── ui/
│   ├── screens/          # 4 ecrãs principais
│   ├── components/       # Componentes reutilizáveis
│   └── theme/           # Cores, tipografia, tema
├── viewmodel/           # ViewModels com StateFlow
├── navigation/          # NavGraph
├── MainActivity.kt
└── GreenRouteApp.kt
```

## 🗄️ Base de Dados

9 Tabelas Room:
- `user` - Perfil do utilizador
- `user_preferences` - Preferências
- `routes` - Histórico de viagens
- `saved_places` - Locais favoritos
- `eco_stats` - Estatísticas ecológicas
- `eco_recommendations` - Recomendações
- `achievements` - Conquistas
- `emission_factors` - Fatores CO₂ por transporte
- `weather_cache` - Cache meteorológico

## 🎨 Design System

- **Cor Principal**: Verde (#2E7D32, #66BB6A)
- **Tipografia**: Roboto/Sans-serif
- **Componentes**: Cards arredondados, Material 3

## 📄 Licença

MIT License

---

Desenvolvido com 💚 para um futuro mais sustentável.
