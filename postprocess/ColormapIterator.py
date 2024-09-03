
class ColormapIterator:
    def __init__(self, cmap):
        self._cmap = cmap
        self.cmap = iter(cmap[:])

    def __iter__(self):
        while True:
            try:
                yield next(self.cmap)
            except StopIteration:
                self.cmap = iter(self._cmap[:])

    def __next__(self):
        return next(self.__iter__())
