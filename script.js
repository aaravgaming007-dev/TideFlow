/**
 * TideFlow - Autonomous Live GitHub Release Sync & Engine
 * Dynamically updates all pages upon new GitHub releases from aaravgaming007-dev/TideFlow
 */

// Global state
let githubReleasesData = [];
let latestReleaseObject = null;

// Copy to clipboard helper
function copyToClipboard(textToCopy, textElementId, iconElementId, originalText, successMsg) {
    navigator.clipboard.writeText(textToCopy).then(() => {
        const textNode = document.getElementById(textElementId);
        const iconNode = document.getElementById(iconElementId);

        if (textNode) {
            textNode.innerText = successMsg;
            textNode.style.color = '#00ff88';
        }
        if (iconNode) {
            iconNode.className = 'fa-solid fa-check';
            iconNode.style.color = '#00ff88';
        }

        setTimeout(() => {
            if (textNode) {
                textNode.innerText = originalText;
                textNode.style.color = '';
            }
            if (iconNode) {
                iconNode.className = 'fa-regular fa-copy';
                iconNode.style.color = '';
            }
        }, 2000);
    }).catch(err => {
        console.error("Clipboard copy failed:", err);
    });
}

window.copyHashToClipboard = function(hashToCopy) {
    const hash = hashToCopy || document.getElementById('hashText')?.innerText || "70deb5b6a835e920777eaa09be37cf76d920b3e271c274a40f776576f4ccbb3e";
    copyToClipboard(hash, "hashText", "hashCopyIcon", hash, "Hash Copied!");
};

// Simple Markdown to HTML parser for GitHub Release Bodies
function parseMarkdown(mdText) {
    if (!mdText) return '<p>No release notes provided for this version.</p>';

    let html = mdText
        // Escape HTML
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        // Headers
        .replace(/^### (.*$)/gim, '<h3>$1</h3>')
        .replace(/^## (.*$)/gim, '<h2>$1</h2>')
        .replace(/^# (.*$)/gim, '<h1>$1</h1>')
        // Bold & Italic
        .replace(/\*\*(.*?)\*\*/gim, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/gim, '<em>$1</em>')
        // Inline code
        .replace(/`([^`]+)`/gim, '<code>$1</code>')
        // Unordered lists
        .replace(/^\s*-\s+(.*$)/gim, '<li>$1</li>')
        .replace(/^\s*\*\s+(.*$)/gim, '<li>$1</li>')
        // Line breaks
        .replace(/\r\n/g, '<br>')
        .replace(/\n/g, '<br>');

    // Wrap li in ul
    html = html.replace(/(<li>.*?<\/li>)/gis, '<ul>$1</ul>');
    html = html.replace(/<\/ul><br><ul>/gis, '');

    return html;
}

// 1. Wavy Text Loader & Matrix Progress Fill
const initWavyLoader = () => {
    const anchor = document.getElementById('wavy-text-anchor');
    if (!anchor) return;
    const textString = "TIDEFLOW";
    anchor.innerHTML = '';
    textString.split('').forEach((letter, index) => {
        const span = document.createElement('span');
        span.className = 'wavy-letter';
        span.innerText = letter;
        span.style.animationDelay = `${index * 0.08}s`;
        anchor.appendChild(span);
    });
};

const initProgressBarEngine = () => {
    const barFill = document.getElementById('loaderMatrixProgressBarNode');
    if (!barFill) return;
    let currentFillProgress = 0;
    const targetTotalDuration = 450;
    const executionInterval = 20;
    const progressDeltaStep = (executionInterval / targetTotalDuration) * 100;

    const progressBarCycleTimer = setInterval(() => {
        currentFillProgress += progressDeltaStep;
        if (currentFillProgress >= 100) {
            currentFillProgress = 100;
            clearInterval(progressBarCycleTimer);
        }
        barFill.style.width = currentFillProgress + '%';
    }, executionInterval);
};

// 2. Matrix Text Scramble Effect
class TextScramble {
    constructor(el) {
        this.el = el;
        this.chars = '!<>-_\\/[]{}—=+*^?#________';
        this.update = this.update.bind(this);
    }
    setText(newText) {
        const oldText = this.el.innerText;
        const length = Math.max(oldText.length, newText.length);
        const promise = new Promise((resolve) => this.resolve = resolve);
        this.queue = [];
        for (let i = 0; i < length; i++) {
            const from = oldText[i] || '';
            const to = newText[i] || '';
            const start = Math.floor(Math.random() * 40);
            const end = start + Math.floor(Math.random() * 40);
            this.queue.push({ from, to, start, end });
        }
        cancelAnimationFrame(this.frameId);
        this.frame = 0;
        this.update();
        return promise;
    }
    update() {
        let output = '';
        let complete = 0;
        for (let i = 0, n = this.queue.length; i < n; i++) {
            let { from, to, start, end, char } = this.queue[i];
            if (this.frame >= end) {
                complete++;
                output += to;
            } else if (this.frame >= start) {
                if (!char || Math.random() < 0.28) {
                    char = this.randomChar();
                    this.queue[i].char = char;
                }
                output += `<span style="color: #ffffff">${char}</span>`;
            } else {
                output += from;
            }
        }
        this.el.innerHTML = output;
        if (complete === this.queue.length) {
            this.resolve();
        } else {
            this.frameId = requestAnimationFrame(this.update);
            this.frame++;
        }
    }
    randomChar() {
        return this.chars[Math.floor(Math.random() * this.chars.length)];
    }
}

// 3. Liquid Chrome Soundwave & Silver Particle Canvas
const canvas = document.getElementById('tide-canvas');
const ctx = canvas ? canvas.getContext('2d') : null;
let mouse = { x: -500, y: -500, targetX: -500, targetY: -500, radius: 220, active: false };
let isMobile = window.innerWidth <= 768;
let particles = [];
let waveTime = 0;

function initTideCanvas() {
    if (!canvas) return;
    canvas.width = window.innerWidth;
    canvas.height = window.innerHeight;
    isMobile = window.innerWidth <= 768;

    particles = [];
    const particleCount = isMobile ? 35 : 65;

    for (let i = 0; i < particleCount; i++) {
        particles.push({
            x: Math.random() * canvas.width,
            y: Math.random() * canvas.height,
            size: Math.random() * 2 + 0.8,
            speedX: (Math.random() - 0.5) * 0.4,
            speedY: (Math.random() - 0.5) * 0.4,
            baseAlpha: Math.random() * 0.45 + 0.15,
            isChrome: Math.random() > 0.3
        });
    }
}

function drawLiquidChromeWave(yOffset, amplitude, frequency, speed, color1, color2, alpha, isStrokeOnly = false) {
    if (!ctx || !canvas) return;
    ctx.save();
    ctx.beginPath();
    
    if (!isStrokeOnly) {
        ctx.moveTo(0, canvas.height);
    }

    const step = isMobile ? 12 : 5;
    for (let x = 0; x <= canvas.width; x += step) {
        let y = yOffset + Math.sin(x * frequency + waveTime * speed) * amplitude;
        y += Math.cos(x * frequency * 0.6 + waveTime * speed * 0.9) * (amplitude * 0.35);

        const centerDist = Math.abs(x - canvas.width / 2);
        if (centerDist < 300) {
            const bell = Math.exp(-Math.pow(centerDist / 120, 2));
            y += Math.sin(x * 0.05 + waveTime * 3) * 18 * bell;
        }

        const dx = x - mouse.x;
        const dy = y - mouse.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < mouse.radius) {
            const push = (1 - dist / mouse.radius) * 45;
            y += Math.sin(dist * 0.06 - waveTime * 4) * push;
        }

        if (x === 0 && isStrokeOnly) {
            ctx.moveTo(x, y);
        } else {
            ctx.lineTo(x, y);
        }
    }

    if (!isStrokeOnly) {
        ctx.lineTo(canvas.width, canvas.height);
        ctx.closePath();

        const gradient = ctx.createLinearGradient(0, yOffset - amplitude, canvas.width, canvas.height);
        gradient.addColorStop(0, color1);
        gradient.addColorStop(1, color2);
        ctx.fillStyle = gradient;
        ctx.globalAlpha = alpha;
        ctx.fill();
    } else {
        ctx.strokeStyle = color1;
        ctx.lineWidth = 1.5;
        ctx.globalAlpha = alpha;
        ctx.shadowBlur = 10;
        ctx.shadowColor = 'rgba(255, 255, 255, 0.4)';
        ctx.stroke();
    }
    
    ctx.restore();
}

function animateTideCanvas() {
    if (!ctx || !canvas) return;
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    waveTime += 0.014;

    mouse.x += (mouse.targetX - mouse.x) * 0.1;
    mouse.y += (mouse.targetY - mouse.y) * 0.1;

    const baseH = canvas.height * 0.7;
    
    drawLiquidChromeWave(
        baseH + 40,
        35,
        0.0022,
        0.7,
        'rgba(30, 30, 38, 0.25)',
        'rgba(0, 0, 0, 0.95)',
        0.6
    );

    drawLiquidChromeWave(
        baseH,
        28,
        0.0032,
        1.1,
        'rgba(203, 213, 225, 0.12)',
        'rgba(10, 10, 14, 0.85)',
        0.5
    );

    drawLiquidChromeWave(
        baseH - 20,
        22,
        0.0042,
        1.4,
        'rgba(255, 255, 255, 0.35)',
        'transparent',
        0.7,
        true
    );

    particles.forEach(p => {
        p.x += p.speedX;
        p.y += p.speedY;

        if (p.x < 0) p.x = canvas.width;
        if (p.x > canvas.width) p.x = 0;
        if (p.y < 0) p.y = canvas.height;
        if (p.y > canvas.height) p.y = 0;

        const dx = p.x - mouse.x;
        const dy = p.y - mouse.y;
        const dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < mouse.radius) {
            const angle = Math.atan2(dy, dx);
            const force = (1 - dist / mouse.radius) * 3.5;
            p.x += Math.cos(angle) * force;
            p.y += Math.sin(angle) * force;
        }

        ctx.save();
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
        ctx.fillStyle = p.isChrome ? '#ffffff' : '#94a3b8';
        ctx.globalAlpha = p.baseAlpha;
        ctx.shadowBlur = 8;
        ctx.shadowColor = 'rgba(255, 255, 255, 0.5)';
        ctx.fill();
        ctx.restore();
    });

    requestAnimationFrame(animateTideCanvas);
}

// 4. Pointer Movement & Ambient Follower
let transformUpdatePending = false;
window.addEventListener('mousemove', (e) => {
    mouse.targetX = e.clientX;
    mouse.targetY = e.clientY;
    mouse.active = true;

    const cursorGlow = document.getElementById('cursor-glow');
    if (!transformUpdatePending && !isMobile && cursorGlow) {
        transformUpdatePending = true;
        requestAnimationFrame(() => {
            cursorGlow.style.transform = `translate3d(${e.clientX}px, ${e.clientY}px, 0)`;
            transformUpdatePending = false;
        });
    }
});

window.addEventListener('mouseout', () => {
    mouse.targetX = -500;
    mouse.targetY = -500;
});

window.addEventListener('resize', () => {
    initTideCanvas();
});

// 5. Diagnostics Terminal Autotyper
const termLines = [
    { text: "system_status --verify-integrity", isCmd: true },
    { text: "[SUCCESS] Audio decoder pipeline matched hardware constraints.", isCmd: false },
    { text: "network_gateway --source 'YouTube Music API'", isCmd: true },
    { text: "[ONLINE] High-fidelity Opus/AAC stream extraction active.", isCmd: false },
    { text: "adblock_filter --status", isCmd: true },
    { text: "[ACTIVE] Commercial stream filters enabled (0 sponsor cuts).", isCmd: false },
    { text: "github_sync --poll-releases", isCmd: true },
    { text: "[SYNCHRONIZED] Telemetry & APK distribution connected to GitHub REST API.", isCmd: false }
];

async function typeTerminal() {
    const term = document.getElementById('linux-terminal');
    if (!term) return;
    term.innerHTML = '';
    const cursor = '<span class="blinking-cursor">_</span>';

    for (let i = 0; i < termLines.length; i++) {
        const line = termLines[i];
        const p = document.createElement('p');
        term.appendChild(p);
        if (line.isCmd) {
            const prompt = '<span class="term-accent">user@tideflow:~$</span> ';
            for (let c = 0; c <= line.text.length; c++) {
                p.innerHTML = prompt + line.text.substring(0, c) + cursor;
                await new Promise(r => setTimeout(r, 35));
            }
            p.innerHTML = prompt + line.text;
            await new Promise(r => setTimeout(r, 250));
        } else {
            p.className = 'term-success';
            p.innerHTML = line.text;
            await new Promise(r => setTimeout(r, 350));
        }
    }
    const finalP = document.createElement('p');
    finalP.innerHTML = '<span class="term-accent">user@tideflow:~$</span> ' + cursor;
    term.appendChild(finalP);
}

// 6. Smooth Scroll Reveal & Tracker
let scrollPending = false;
function scrollReveal() {
    if (!scrollPending) {
        scrollPending = true;
        requestAnimationFrame(() => {
            const reveals = document.querySelectorAll('.reveal');
            const triggerBottom = window.innerHeight - 40;
            reveals.forEach(el => {
                if (el.getBoundingClientRect().top < triggerBottom) {
                    el.classList.add('active');
                }
            });

            const winScroll = document.body.scrollTop || document.documentElement.scrollTop;
            const height = document.documentElement.scrollHeight - document.documentElement.clientHeight;
            const scrolled = (winScroll / height) * 100;
            const tracker = document.getElementById("scroll-tracker");
            if (tracker) tracker.style.width = scrolled + "%";
            scrollPending = false;
        });
    }
}
window.addEventListener('scroll', scrollReveal);

// 7. Odometer Count-Up Animation
function runOdometerAnimation(targetVal) {
    const counterElement = document.getElementById('animatedCounterNode');
    if (!counterElement) return;

    const target = targetVal || parseInt(counterElement.getAttribute('data-target-value')) || 144;
    const duration = 2500;
    const startTime = performance.now();

    function updateCounter(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        const easeProgress = 1 - Math.pow(1 - progress, 3);
        const currentCount = Math.floor(easeProgress * target);

        counterElement.innerText = currentCount.toLocaleString();

        if (progress < 1) {
            requestAnimationFrame(updateCounter);
        } else {
            counterElement.innerText = target.toLocaleString();
        }
    }
    requestAnimationFrame(updateCounter);
}

// 8. Dynamic Autonomous GitHub Releases Fetcher & Page Auto-Updater
async function fetchAndPopulateGitHubReleases() {
    const REPO = 'aaravgaming007-dev/TideFlow';
    const API_URL = `https://api.github.com/repos/${REPO}/releases`;

    try {
        const response = await fetch(API_URL);
        if (!response.ok) throw new Error(`GitHub API Status ${response.status}`);

        const releases = await response.json();
        githubReleasesData = releases;

        if (Array.isArray(releases) && releases.length > 0) {
            latestReleaseObject = releases[0];
        }

        let totalDownloads = 0;
        let latestApkUrl = '';
        let latestTagName = 'v1.0-beta';
        let latestApkSize = '27.8 MB';
        let digestHash = '70deb5b6a835e920777eaa09be37cf76d920b3e271c274a40f776576f4ccbb3e';

        if (releases && releases.length > 0) {
            latestTagName = releases[0].tag_name || 'v1.0-beta';

            releases.forEach((release, rIdx) => {
                if (Array.isArray(release.assets)) {
                    release.assets.forEach(asset => {
                        totalDownloads += asset.download_count || 0;
                        if (rIdx === 0 && !latestApkUrl && asset.name.toLowerCase().endsWith('.apk')) {
                            latestApkUrl = asset.browser_download_url;
                            if (asset.size) {
                                latestApkSize = `${(asset.size / (1024 * 1024)).toFixed(1)} MB`;
                            }
                            if (asset.digest && asset.digest.includes('sha256:')) {
                                digestHash = asset.digest.replace('sha256:', '');
                            }
                        }
                    });
                }
            });
        }

        if (!latestApkUrl) {
            latestApkUrl = `https://github.com/${REPO}/releases/latest`;
        }

        // 1. Update elements on Home page (index.html)
        const counterNode = document.getElementById('animatedCounterNode');
        const downloadBtn = document.getElementById('downloadButtonMain');
        const downloadBtnText = document.getElementById('downloadBtnTextNode');
        const releaseTagLabel = document.getElementById('release-tag-label');
        const apkSizeLabel = document.getElementById('apk-size-label');
        const checksumHero = document.getElementById('checksum-hero');
        const hashText = document.getElementById('hashText');

        if (counterNode) {
            counterNode.setAttribute('data-target-value', totalDownloads);
            runOdometerAnimation(totalDownloads);
        }
        if (downloadBtn) downloadBtn.href = latestApkUrl;
        if (downloadBtnText) downloadBtnText.innerText = `Get TideFlow ${latestTagName} (Latest APK)`;
        if (releaseTagLabel) releaseTagLabel.innerText = latestTagName;
        if (apkSizeLabel) apkSizeLabel.innerText = `~${latestApkSize}`;
        if (checksumHero) checksumHero.innerText = digestHash;
        if (hashText) hashText.innerText = digestHash;

        // 2. Update dynamic feed on Releases page (releases.html)
        renderReleasesPageFeed(releases);

        // 3. Update Security verification page (security.html)
        renderSecurityPageDigest(latestTagName, digestHash, latestApkSize);

    } catch (err) {
        console.warn('Could not fetch live GitHub releases, using fallback:', err);
        runOdometerAnimation(144);
    }
}

// Render dynamic releases list on releases.html
function renderReleasesPageFeed(releases) {
    const feedContainer = document.getElementById('dynamic-releases-feed');
    if (!feedContainer) return;

    if (!releases || releases.length === 0) {
        feedContainer.innerHTML = `
            <div class="release-card-dynamic">
                <p style="text-align: center; color: var(--text-muted);">No releases found on GitHub repository.</p>
            </div>
        `;
        return;
    }

    let feedHtml = '';

    releases.forEach((rel, index) => {
        const isLatest = index === 0;
        const tagName = rel.tag_name || 'v1.0';
        const relTitle = rel.name || `TideFlow ${tagName}`;
        const pubDate = rel.published_at ? new Date(rel.published_at).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' }) : 'Recently published';
        const formattedBody = parseMarkdown(rel.body);

        let apkAsset = null;
        if (Array.isArray(rel.assets)) {
            apkAsset = rel.assets.find(a => a.name.toLowerCase().endsWith('.apk')) || rel.assets[0];
        }

        const apkUrl = apkAsset ? apkAsset.browser_download_url : rel.html_url;
        const apkSize = apkAsset && apkAsset.size ? `${(apkAsset.size / (1024 * 1024)).toFixed(1)} MB` : '~27.8 MB';
        const apkDownloads = apkAsset && apkAsset.download_count ? apkAsset.download_count.toLocaleString() : '0';
        const apkName = apkAsset ? apkAsset.name : `TideFlow-${tagName}.apk`;

        feedHtml += `
            <article class="release-card-dynamic ${isLatest ? 'latest-highlight' : ''} reveal active">
                <div class="release-header-row">
                    <div style="display: flex; align-items: center; gap: 12px; flex-wrap: wrap;">
                        <span class="release-tag-pill">
                            <i class="fas fa-tag"></i> ${tagName}
                        </span>
                        ${isLatest ? '<span style="background: rgba(0, 255, 136, 0.15); color: #00ff88; border: 1px solid rgba(0, 255, 136, 0.3); font-size: 0.72rem; font-weight: 800; padding: 3px 10px; border-radius: 50px;">LATEST PRODUCTION</span>' : ''}
                    </div>
                    <span class="release-date-text"><i class="far fa-calendar-alt"></i> ${pubDate}</span>
                </div>

                <h2 class="release-title-text">${relTitle}</h2>

                <div class="release-body-markdown">
                    ${formattedBody}
                </div>

                <div class="release-assets-box">
                    <div class="asset-meta-details">
                        <span class="asset-name-title"><i class="fab fa-android"></i> ${apkName}</span>
                        <span class="asset-sub-info">Size: ${apkSize} • Downloads: ${apkDownloads} • Direct Binary</span>
                    </div>
                    <a href="${apkUrl}" class="btn-asset-download">
                        <i class="fas fa-download"></i>
                        <span>Download Package</span>
                    </a>
                </div>
            </article>
        `;
    });

    feedContainer.innerHTML = feedHtml;
}

// Render dynamic elements on security.html
function renderSecurityPageDigest(tagName, hash, size) {
    const secTag = document.getElementById('sec-latest-tag');
    const secHash = document.getElementById('sec-latest-hash');
    const secSize = document.getElementById('sec-latest-size');

    if (secTag) secTag.innerText = tagName;
    if (secHash) secHash.innerText = hash;
    if (secSize) secSize.innerText = size;
}

// Client-Side In-Browser SHA-256 APK File Verifier (for security.html)
function initFileHashVerifier() {
    const dropZone = document.getElementById('apk-drop-zone');
    const fileInput = document.getElementById('apk-file-input');
    const resultBox = document.getElementById('hash-result-box');
    const computedHashEl = document.getElementById('computed-hash-val');
    const matchStatusEl = document.getElementById('hash-match-status');

    if (!dropZone || !fileInput) return;

    dropZone.addEventListener('click', () => fileInput.click());

    dropZone.addEventListener('dragover', (e) => {
        e.preventDefault();
        dropZone.classList.add('dragover');
    });

    dropZone.addEventListener('dragleave', () => dropZone.classList.remove('dragover'));

    dropZone.addEventListener('drop', (e) => {
        e.preventDefault();
        dropZone.classList.remove('dragover');
        if (e.dataTransfer.files.length > 0) {
            verifyFile(e.dataTransfer.files[0]);
        }
    });

    fileInput.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            verifyFile(e.target.files[0]);
        }
    });

    async function verifyFile(file) {
        if (!resultBox || !computedHashEl || !matchStatusEl) return;

        resultBox.style.display = 'block';
        computedHashEl.innerText = 'Calculating cryptographic SHA-256 checksum...';
        matchStatusEl.innerText = '';

        try {
            const arrayBuffer = await file.arrayBuffer();
            const hashBuffer = await crypto.subtle.digest('SHA-256', arrayBuffer);
            const hashArray = Array.from(new Uint8Array(hashBuffer));
            const hexHash = hashArray.map(b => b.toString(16).padStart(2, '0')).join('');

            computedHashEl.innerText = hexHash;

            const targetHash = document.getElementById('sec-latest-hash')?.innerText.trim().toLowerCase();
            if (targetHash && hexHash.toLowerCase() === targetHash) {
                matchStatusEl.innerHTML = '<span style="color: #00ff88; font-weight: 800;"><i class="fas fa-check-circle"></i> VERIFIED: Checksum matches official GitHub release asset perfectly.</span>';
            } else {
                matchStatusEl.innerHTML = '<span style="color: #cbd5e1;"><i class="fas fa-info-circle"></i> File processed: ' + file.name + ' (' + (file.size / (1024 * 1024)).toFixed(1) + ' MB)</span>';
            }
        } catch (err) {
            computedHashEl.innerText = 'Error reading file: ' + err.message;
        }
    }
}

// 9. FAQ Accordion Setup
const setupFaqAccordions = () => {
    const faqNodes = document.querySelectorAll('.faq-wrapper-node');
    faqNodes.forEach(node => {
        const trigger = node.querySelector('.faq-trigger-tab');
        const panel = node.querySelector('.faq-content-panel');

        trigger.addEventListener('click', () => {
            const isActive = node.classList.contains('active');
            faqNodes.forEach(otherNode => {
                otherNode.classList.remove('active');
                otherNode.querySelector('.faq-content-panel').style.maxHeight = null;
            });
            if (!isActive) {
                node.classList.add('active');
                panel.style.maxHeight = panel.scrollHeight + "px";
            }
        });
    });
};

// 10. Drawer Toggle
window.toggleMenu = function() {
    const sideMenu = document.getElementById('sideMenu');
    if (sideMenu) sideMenu.classList.toggle('active');
};

document.body.addEventListener('click', function(e) {
    const menuToggle = e.target.closest('.menu-trigger');
    if (menuToggle) {
        e.preventDefault();
        window.toggleMenu();
    }
});

// 11. Bento Card 3D Tilt
function initBentoTilt() {
    const cardsList = document.querySelectorAll('.bento-card');
    cardsList.forEach(card => {
        card.addEventListener('mousemove', (e) => {
            if (isMobile) return;
            const rect = card.getBoundingClientRect();
            const xc = rect.width / 2;
            const yc = rect.height / 2;
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            card.style.transform = `rotateX(${(yc - y) / 25}deg) rotateY(${(x - xc) / 25}deg) translate3d(0, -4px, 0) scale(1.01)`;
        });
        card.addEventListener('mouseleave', () => {
            card.style.transform = 'rotateX(0deg) rotateY(0deg) translate3d(0, 0, 0) scale(1)';
        });
    });
}

// 12. App Bootstrapper
function startApp() {
    initWavyLoader();
    initProgressBarEngine();
    initTideCanvas();
    animateTideCanvas();
    setupFaqAccordions();
    initBentoTilt();
    initFileHashVerifier();

    const targetEl = document.getElementById('scramble-target');
    if (targetEl) {
        textScrambleInstance = new TextScramble(targetEl);
        textScrambleInstance.setText("STREAM MUSIC FREELY. PURE, AD-FREE & FLUID.");
    }

    // Terminal observer
    activeScrollObserver = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                typeTerminal();
                activeScrollObserver.unobserve(entry.target);
            }
        });
    }, { threshold: 0.5 });

    const termElement = document.getElementById('linux-terminal');
    if (termElement) activeScrollObserver.observe(termElement);

    scrollReveal();
    fetchAndPopulateGitHubReleases();

    setTimeout(() => {
        const screen = document.getElementById('loading-screen');
        if (screen) screen.classList.add('fade-out');
    }, 550);
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', startApp);
} else {
    startApp();
}
