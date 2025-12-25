# GitHub Setup Instructions

## Step 1: Create Repository on GitHub

1. Go to https://github.com/Smughito/EventCoreLib
2. If it doesn't exist, click the "+" icon in the top right and select "New repository"
3. Name it `EventCoreLib`
4. Do NOT initialize with README, .gitignore, or license (we already have these)
5. Click "Create repository"

## Step 2: Push Code to GitHub

Open your terminal and navigate to the EventCoreLib directory, then run:

```bash
cd /path/to/EventCoreLib

# Initialize git repository
git init

# Add all files
git add .

# Make initial commit
git commit -m "Initial commit: EventCoreLib v1.0.0"

# Add GitHub remote
git remote add origin https://github.com/Smughito/EventCoreLib.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## Step 3: Verify GitHub Actions

1. Go to your repository on GitHub
2. Click the "Actions" tab
3. You should see a workflow run automatically
4. Once complete, the built JAR will be available as an artifact

## Step 4: Create a Release

1. Go to the "Releases" section of your repository
2. Click "Create a new release"
3. Tag version: `v1.0.0`
4. Release title: `EventCoreLib v1.0.0`
5. Upload the built JAR file from the Actions artifacts
6. Add release notes describing the features
7. Click "Publish release"

## Alternative: Using GitHub CLI

If you have GitHub CLI installed:

```bash
cd /path/to/EventCoreLib
git init
git add .
git commit -m "Initial commit: EventCoreLib v1.0.0"
git branch -M main
gh repo create Smughito/EventCoreLib --public --source=. --push
```

## Troubleshooting

### Authentication Failed
You may need to set up a Personal Access Token:
1. Go to GitHub Settings > Developer settings > Personal access tokens
2. Generate new token with `repo` scope
3. Use the token as your password when pushing

### Remote Already Exists
If you get an error about the remote already existing:
```bash
git remote remove origin
git remote add origin https://github.com/Smughito/EventCoreLib.git
```
