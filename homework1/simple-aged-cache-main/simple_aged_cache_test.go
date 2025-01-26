package cache_test

import (
	"github.com/stretchr/testify/assert"
	cache "github.io/coloradocollective/simple-aged-cache"
	"testing"
	"time"
)

func setUp() (*cache.SimpleAgedCache, *cache.SimpleAgedCache) {
	empty := cache.New()
	nonempty := cache.New()

	nonempty.Put("a_key", "a_value", 2*time.Second)
	nonempty.Put("another_key", "another_value", 4*time.Second)

	return empty, nonempty
}

func TestSimpleAgedCache_IsEmpty(t *testing.T) {
	empty, nonempty := setUp()

	assert.True(t, empty.IsEmpty())
	assert.False(t, nonempty.IsEmpty())
}

func TestSimpleAgedCache_Size(t *testing.T) {
	empty, nonempty := setUp()

	assert.Equal(t, 0, empty.Size())
	assert.Equal(t, 2, nonempty.Size())
}

func TestSimpleAgedCache_Get(t *testing.T) {
	empty, nonempty := setUp()

	assert.Nil(t, empty.Get("a_key"))
	assert.Equal(t, "a_value", nonempty.Get("a_key"))
	assert.Equal(t, "another_value", nonempty.Get("another_key"))
}

func TestSimpleAgedCache_GetExpired(t *testing.T) {
	testClock := &TestClock{time.Now()}
	expired := cache.NewWithClock(testClock)

	expired.Put("a_key", "a_value", 2*time.Second)
	expired.Put("another_key", "another_value", 4*time.Second)

	testClock.Advance(3 * time.Second)

	assert.Equal(t, 1, expired.Size())
	assert.Nil(t, expired.Get("a_key"))
	assert.Equal(t, "another_value", expired.Get("another_key"))
}

type TestClock struct {
	now time.Time
}

func (c *TestClock) Now() time.Time {
	return c.now
}

func (c *TestClock) Advance(amount time.Duration) {
	c.now = c.now.Add(amount)
}
