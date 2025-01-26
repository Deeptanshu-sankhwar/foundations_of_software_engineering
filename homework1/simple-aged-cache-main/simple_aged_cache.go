package cache

import (
	"sync"
	"time"
)

type SimpleAgedCache struct {
	clock Clock
	data  map[string]entry
	mu    sync.Mutex
}

type entry struct {
	value      any
	expiryTime time.Time
}

func New() *SimpleAgedCache {
	return &SimpleAgedCache{
		clock: DefaultClock{},
		data:  make(map[string]entry),
	}
}

func NewWithClock(clock Clock) *SimpleAgedCache {
	return &SimpleAgedCache{
		clock: clock,
		data:  make(map[string]entry),
	}
}

func (c *SimpleAgedCache) Put(key string, value any, retention time.Duration) {
	c.mu.Lock()
	defer c.mu.Unlock()

	expiry := c.clock.Now().Add(retention)
	c.data[key] = entry{
		value:      value,
		expiryTime: expiry,
	}
}

func (c *SimpleAgedCache) IsEmpty() bool {
	c.mu.Lock()
	defer c.mu.Unlock()

	c.cleanupExpired()
	return len(c.data) == 0
}

func (c *SimpleAgedCache) Size() int {
	c.mu.Lock()
	defer c.mu.Unlock()

	c.cleanupExpired()
	return len(c.data)
}

func (c *SimpleAgedCache) Get(key string) any {
	c.mu.Lock()
	defer c.mu.Unlock()

	if e, exists := c.data[key]; exists {
		if c.clock.Now().Before(e.expiryTime) {
			return e.value
		}
		// Remove expired entry
		delete(c.data, key)
	}
	return nil
}

func (c *SimpleAgedCache) cleanupExpired() {
	now := c.clock.Now()
	for key, e := range c.data {
		if now.After(e.expiryTime) {
			delete(c.data, key)
		}
	}
}

type Clock interface {
	Now() time.Time
}

type DefaultClock struct{}

func (c DefaultClock) Now() time.Time {
	return time.Now()
}
